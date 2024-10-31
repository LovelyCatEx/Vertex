package com.lovelycatv.vertex.work

import com.lovelycatv.vertex.work.base.AbstractWork
import com.lovelycatv.vertex.work.base.AbstractWorkChainInterceptor
import com.lovelycatv.vertex.work.scope.WorkChainCoroutineScope
import com.lovelycatv.vertex.work.scope.WorkCoroutineScope
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

    fun getWorkById(workId: String): AbstractWork? {
        for ((_, workScope) in workScopes) {
            for ((workInstance, _) in workScope.getStartedJobsMap()) {
                if (workInstance.workName == workId) {
                    return workInstance
                }
            }
        }
        return null
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
        // Find a empty WorkCoroutineScope
        val (_, workCoroutineScope) = requireEmptyWorkCoroutineScope(coroutineContext, exceptionHandler)

        workCoroutineScope.initialize(workChain.getTotalWorks())

        val scope = WorkChainCoroutineScope()

        scope.launch {
            for ((blockCount, block) in workChain.blocks.withIndex()) {
                interceptor?.beforeBlockStarted(blockCount, block)
                if (!block.isParallel) {
                    // In sequence
                    for ((workCount, work) in block.works.withIndex()) {
                        interceptor?.beforeWorkStarted(blockCount, block, workCount, work)
                        val result = workCoroutineScope.launchTaskAsync(work) {
                            work.startWork()
                        }
                        result.await()
                        // Wait until protected jobs stopped
                        work.waitForProtectedJobs()
                    }
                } else {
                    if (block.parallelInBound) {
                        // Parallel in bound
                        val deferred = block.works.mapIndexed { index, work ->
                            interceptor?.beforeWorkStarted(blockCount, block, index, work)
                            workCoroutineScope.launchTaskAsync(work) {
                                work.startWork()
                            }
                        }

                        deferred.awaitAll()
                        // Wait until protected jobs stopped
                        block.works.forEach {
                            it.waitForProtectedJobs()
                        }
                    } else {
                        // Parallel
                        block.works.forEachIndexed { index, work ->
                            interceptor?.beforeWorkStarted(blockCount, block, index, work)
                            workCoroutineScope.launchTask(work) {
                                work.startWork()
                            }
                        }
                    }
                }
            }
        }
        return scope to workCoroutineScope
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