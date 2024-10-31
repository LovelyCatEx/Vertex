package com.lovelycatv.vertex.work

import com.lovelycatv.vertex.extension.runCoroutineAsync
import com.lovelycatv.vertex.work.interceptor.AbstractWorkChainInterceptor
import com.lovelycatv.vertex.work.base.WrappedWorker
import com.lovelycatv.vertex.work.exception.DuplicateWorkerIdException
import com.lovelycatv.vertex.work.scope.WorkChainCoroutineScope
import com.lovelycatv.vertex.work.scope.WorkCoroutineScope
import com.lovelycatv.vertex.work.scope.WorkExceptionHandler
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.CoroutineContext

/**
 * @author lovelycat
 * @since 2024-10-27 19:40
 * @version 1.0
 */
class WorkManager {
    private val workScopes = mutableMapOf<String, WorkCoroutineScope>()

    private fun getWorkersBy(condition: (WrappedWorker) -> Boolean): List<WrappedWorker> {
        val result = mutableListOf<WrappedWorker>()
        for ((_, scope) in workScopes) {
            scope.getStartedJobsMap().forEach { (worker, _) ->
                if (condition(worker)) {
                    result.add(worker)
                }
            }
        }
        return result
    }

    private fun getWorkerById(workerId: String): WrappedWorker? {
        return getWorkersBy {
            it.getWorkerId() == workerId
        }.run { if (this.isEmpty()) null else this[0] }
    }

    private fun getWorkersByName(workerName: String): List<WrappedWorker> {
        return getWorkersBy {
            it.getWorker().workName == workerName
        }
    }

    private fun requireEmptyWorkCoroutineScope(
        context: CoroutineContext = Dispatchers.IO,
        exceptionHandler: WorkExceptionHandler? = null
    ): Pair<String, WorkCoroutineScope> {
        for ((scopeId, scope) in workScopes) {
            if (scope.isAvailable()) {
                println("Existing $scopeId")
                return scopeId to scope.apply {
                    // If the given exceptionHandler is not null, replace the original with this
                    if (exceptionHandler != null) {
                        this.exceptionHandler = exceptionHandler
                    }
                }
            }
        }
        // There are no empty WorkCoroutineScope, create one
        val newScopeId = UUID.randomUUID().toString()
        val newScope = WorkCoroutineScope(context, exceptionHandler)
        println("Created $newScopeId")
        workScopes[newScopeId] = newScope
        return newScopeId to newScope
    }

    fun runWorkChain(
        workChain: WorkChain,
        coroutineContext: CoroutineContext = Dispatchers.IO,
        exceptionHandler: WorkExceptionHandler? = null,
        interceptor: AbstractWorkChainInterceptor? = null
    ): Pair<WorkChainCoroutineScope, WorkCoroutineScope> {
        // Validate workerId
        val existingWorkers = workChain.blocks.flatMap { it.works }.mapNotNull {
            this.getWorkerById(it.getWorkerId())
        }

        if (existingWorkers.isNotEmpty()) {
            throw DuplicateWorkerIdException(existingWorkers[0].getWorkerId())
        }

        // Find a empty WorkCoroutineScope
        val (_, workCoroutineScope) = requireEmptyWorkCoroutineScope(coroutineContext, exceptionHandler)

        workCoroutineScope.initialize(workChain.getTotalWorks())

        val scope = WorkChainCoroutineScope()

        scope.launch {
            for ((blockCount, block) in workChain.blocks.withIndex()) {
                interceptor?.beforeBlockStarted(blockCount, block)
                if (!block.isParallel) {
                    // In sequence
                    for (work in block.works) {
                        interceptor?.beforeWorkStarted(blockCount, block, work)
                        val finalResult = runWorkWithRetry(workCoroutineScope, work)
                        if (!finalResult.isCompletedOrStopped()) {
                            if (work.getFailureStrategy() == WorkFailureStrategy.INTERRUPT_BLOCK) {
                                interceptor?.onBlockInterrupted(
                                    blockIndex = blockCount,
                                    block = block,
                                    producer = work
                                )
                                break
                            } else if (work.getFailureStrategy() == WorkFailureStrategy.INTERRUPT_CHAIN) {
                                interceptor?.onChainInterrupted(
                                    blockIndex = blockCount,
                                    block = block,
                                    producer = work
                                )
                                return@launch
                            }
                        }
                    }
                } else {
                    val fxHasFailedWorks = fun (results: List<WorkResult>): List<Int> {
                        return results.mapIndexedNotNull { index, it -> if (!it.isCompletedOrStopped()) index else null }
                    }

                    if (block.parallelInBound) {
                        // Parallel in bound
                        val deferred = block.works.map { work ->
                            interceptor?.beforeWorkStarted(blockCount, block, work)
                            workCoroutineScope.launchTaskAsync(work)
                        }

                        val results = deferred.awaitAll()
                        // Check and retry failure works
                        val failedWorks = fxHasFailedWorks(results)
                            .map { block.works[it] }
                            .map {
                                runCoroutineAsync {
                                    runWorkWithRetry(workCoroutineScope, it)
                                }
                            }
                        // Check again and determine whether to do next
                        val finalFailedStrategies = fxHasFailedWorks(failedWorks.awaitAll()).map { block.works[it] }.map { it.getFailureStrategy() }
                        if (finalFailedStrategies.contains(WorkFailureStrategy.INTERRUPT_CHAIN)) {
                            interceptor?.onChainInterrupted(
                                blockIndex = blockCount,
                                block = block,
                                producer = block.works[finalFailedStrategies.indexOf(WorkFailureStrategy.INTERRUPT_CHAIN)]
                            )
                            return@launch
                        } else if (finalFailedStrategies.contains(WorkFailureStrategy.INTERRUPT_BLOCK)) {
                            interceptor?.onBlockInterrupted(
                                blockIndex = blockCount,
                                block = block,
                                producer = block.works[finalFailedStrategies.indexOf(WorkFailureStrategy.INTERRUPT_BLOCK)]
                            )
                            continue
                        }
                    } else {
                        // Parallel
                        block.works.forEach { work ->
                            interceptor?.beforeWorkStarted(blockCount, block, work)
                            workCoroutineScope.launchTask(work)
                        }
                    }
                }
            }
        }

        return scope to workCoroutineScope
    }

    private suspend fun runWorkWithRetry(workScope: WorkCoroutineScope, work: WrappedWorker): WorkResult {
        var result: WorkResult? = null
        var retriedCount = 1
        val maxRetryTimes = work.getRetryStrategy().maxRetryTimes
        if (work.getRetryStrategy().type == WorkRetryStrategy.Type.RETRY) {
            while (result == null || (!result.isCompletedOrStopped() && retriedCount <= maxRetryTimes)) {
                delay(work.getRetryStrategy().retryInterval.invoke(retriedCount))
                result = workScope.launchTaskAsync(work).await()
                retriedCount++
            }
        } else {
            result = workScope.launchTaskAsync(work).await()
        }
        return result
    }

    suspend fun stopWorkChain(workChain: WorkChainCoroutineScope, works: WorkCoroutineScope, reason: String = "") {
        workChain.cancel(reason)
        works.stopCurrentWorks(reason)
        println("WorkChain was stopped for reason: $reason")
    }

    fun forceStopWorkChain(workChain: WorkChainCoroutineScope, works: WorkCoroutineScope, reason: String = "") {
        workChain.cancel(reason)
        works.forceStopCurrentWorks(reason)
        println("WorkChain was force stopped for reason: $reason")
    }
}