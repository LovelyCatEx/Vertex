package com.lovelycatv.vertex.work

import com.lovelycatv.vertex.extension.runCoroutineAsync
import com.lovelycatv.vertex.work.data.WorkData
import com.lovelycatv.vertex.work.interceptor.AbstractWorkChainInterceptor
import com.lovelycatv.vertex.work.worker.WrappedWorker
import com.lovelycatv.vertex.work.data.WorkResult
import com.lovelycatv.vertex.work.exception.DuplicateWorkerIdException
import com.lovelycatv.vertex.work.scope.WorkChainCoroutineScope
import com.lovelycatv.vertex.work.scope.WorkCoroutineScope
import com.lovelycatv.vertex.work.scope.WorkExceptionHandler
import com.lovelycatv.vertex.work.worker.WorkChain
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
            var outputsFromLastBlock: WorkData? = null
            val fxGetOutputsFromLastBlock = fun (): WorkData {
                return outputsFromLastBlock ?: WorkData.build()
            }
            for ((blockCount, block) in workChain.blocks.withIndex()) {
                interceptor?.beforeBlockStarted(blockCount, block)
                if (!block.isParallel) {
                    // In sequence
                    var outputsFromLastSequenceWork: WorkData? = null
                    for ((workIndex, work) in block.works.withIndex()) {
                        interceptor?.beforeWorkStarted(blockCount, block, work)
                        // The outputs from last block should be transmitted to the first work of sequence
                        val inputData = if (workIndex == 0)
                            fxGetOutputsFromLastBlock()
                        else
                            outputsFromLastSequenceWork ?: WorkData.build()
                        val finalResult = runWorkWithRetry(workCoroutineScope, work, inputData)
                        if (!finalResult.isCompletedOrStopped()) {
                            if (work.getFailureStrategy() == WorkFailureStrategy.INTERRUPT_BLOCK) {
                                interceptor?.onBlockInterrupted(
                                    blockIndex = blockCount,
                                    block = block,
                                    producer = work
                                )
                                // The sequence block is end up with failure and the outputs of this block should be null
                                outputsFromLastSequenceWork = null
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
                        outputsFromLastSequenceWork = finalResult.output
                    }
                    // Recognize the outputs from the last work in the sequence as the outputs of this block
                    outputsFromLastBlock = outputsFromLastSequenceWork
                } else {
                    val fxHasFailedWorks = fun (results: List<WorkResult>): List<Int> {
                        return results.mapIndexedNotNull { index, it -> if (!it.isCompletedOrStopped()) index else null }
                    }

                    if (block.parallelInBound) {
                        // Parallel in bound
                        val deferred = block.works.map { work ->
                            interceptor?.beforeWorkStarted(blockCount, block, work)
                            workCoroutineScope.launchTaskAsync(work, fxGetOutputsFromLastBlock())
                        }

                        val results = deferred.awaitAll()
                        // Check and retry failure works
                        val failedWorkIndexes = fxHasFailedWorks(results)
                        val failedWorks = failedWorkIndexes
                            .map { block.works[it] }
                            .map {
                                runCoroutineAsync {
                                    runWorkWithRetry(workCoroutineScope, it, fxGetOutputsFromLastBlock())
                                }
                            }

                        val resultsOfFailureWorks = failedWorks.awaitAll()
                        val indexesOfWorksStillFailed = fxHasFailedWorks(resultsOfFailureWorks)

                        // Check again and determine whether to do next
                        val finalFailedStrategies = indexesOfWorksStillFailed.map { block.works[it] }.map { it.getFailureStrategy() }
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
                            // The parallel in bound block is end up with failure and the outputs of this block should be null
                            outputsFromLastBlock = null
                            continue
                        }

                        // Get the input data merger
                        val merger = block.inputWorkDataMerger
                        // The first batch of successful works
                        val a = results.filterIndexed { index, _ -> index !in failedWorkIndexes }.map { it.output }
                        // The second batch of successful works after retry
                        val b = resultsOfFailureWorks.filterIndexed { index, _ -> index !in indexesOfWorksStillFailed }.map { it.output }
                        // Save the final outputs
                        outputsFromLastBlock = merger.merge(a + b)
                    } else {
                        // Parallel
                        block.works.forEach { work ->
                            interceptor?.beforeWorkStarted(blockCount, block, work)
                            workCoroutineScope.launchTask(work, fxGetOutputsFromLastBlock())
                        }
                        // Parallel Block does not support transmitting data to next block
                        outputsFromLastBlock = null
                    }
                }
            }
        }

        return scope to workCoroutineScope
    }

    private suspend fun runWorkWithRetry(workScope: WorkCoroutineScope, work: WrappedWorker, inputData: WorkData): WorkResult {
        var result: WorkResult? = null
        var retriedCount = 1
        val maxRetryTimes = work.getRetryStrategy().maxRetryTimes
        if (work.getRetryStrategy().type == WorkRetryStrategy.Type.RETRY) {
            while (result == null || (!result.isCompletedOrStopped() && retriedCount <= maxRetryTimes)) {
                delay(work.getRetryStrategy().retryInterval.invoke(retriedCount))
                result = workScope.launchTaskAsync(work, inputData).await()
                retriedCount++
            }
        } else {
            result = workScope.launchTaskAsync(work, inputData).await()
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