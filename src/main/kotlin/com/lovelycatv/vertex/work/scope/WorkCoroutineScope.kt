package com.lovelycatv.vertex.work.scope

import com.lovelycatv.vertex.extension.runCoroutine
import com.lovelycatv.vertex.extension.runCoroutineAsync
import com.lovelycatv.vertex.work.data.WorkData
import com.lovelycatv.vertex.work.WorkResult
import com.lovelycatv.vertex.work.base.WrappedWorker
import com.lovelycatv.vertex.work.exception.WorkCoroutineScopeAwaitTimeoutException
import com.lovelycatv.vertex.work.exception.WorkCoroutineScopeNotInitializedException
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

typealias WorkExceptionHandler = (worker: WrappedWorker, e: Exception) -> Unit


class WorkCoroutineScope(
    private val context: CoroutineContext = Dispatchers.IO,
    var exceptionHandler: WorkExceptionHandler? = null
) : CoroutineScope {
    private val job = Job()

    private val startedJobs = mutableMapOf<WrappedWorker, Job>()

    override val coroutineContext: CoroutineContext
        get() = context + job

    private var expectedJobs = 0

    private fun checkAvailability() {
        if (this.expectedJobs == 0) {
            throw WorkCoroutineScopeNotInitializedException()
        }
    }

    fun launchTask(identifier: WrappedWorker, context: CoroutineContext = EmptyCoroutineContext): Job {
        this.checkAvailability()
        val newJob = runCoroutine(this, context) {
            try {
                identifier.getWorker().startWork(WorkData.build())
            } catch (e: Exception) {
                e.printStackTrace()
                this.exceptionHandler?.invoke(identifier, e)
            }
        }
        startedJobs[identifier] = newJob
        println("Worker [${identifier.getWorker().workName}::${identifier.getWorkerId()}] started")
        return newJob
    }

    fun launchTaskAsync(identifier: WrappedWorker, context: CoroutineContext = EmptyCoroutineContext): Deferred<WorkResult> {
        this.checkAvailability()
        val newJob = runCoroutineAsync(this, context) {
            try {
                identifier.getWorker().startWork(WorkData.build())
            } catch (e: Exception) {
                this.exceptionHandler?.invoke(identifier, e)
                identifier.getWorker().getCurrentWorkResult()
            }
        }
        startedJobs[identifier] = newJob
        println("Worker [${identifier.getWorker().workName}::${identifier.getWorkerId()}] started")
        return newJob
    }

    suspend fun stopCurrentWorks(reason: String = "") {
        getActiveJobs().forEach { (work, workMainJob) ->
            work.getWorker().stopWork(workMainJob, reason)
        }
    }

    fun forceStopCurrentWorks(reason: String = "") {
        getActiveJobs().forEach { (work, workMainJob) ->
            work.getWorker().forceStopWork(workMainJob, reason)
        }
    }

    fun getStartedJobsMap() = this.startedJobs

    fun getActiveJobs() = this.getStartedJobsMap().filter { it.value.isActive || it.key.getWorker().anyProtectJobsRunning() }

    fun getInactiveJobs() = this.getStartedJobsMap().filter { !it.value.isActive && !it.key.getWorker().anyProtectJobsRunning() }

    fun initialize(expectedJobs: Int) {
        this.startedJobs.clear()
        this.expectedJobs = expectedJobs
    }

    fun isAvailable(): Boolean {
        return this.getInactiveJobs().size == this.expectedJobs
    }

    suspend fun await(timeout: Long) {
        val startTime = System.currentTimeMillis()
        while (!this.isAvailable()) {
            if (System.currentTimeMillis() - startTime >= timeout) {
                throw WorkCoroutineScopeAwaitTimeoutException(this, timeout)
            }
            delay(100)
        }
    }
}