package com.lovelycatv.vertex.work.scope

import com.lovelycatv.vertex.extension.runCoroutine
import com.lovelycatv.vertex.extension.runCoroutineAsync
import com.lovelycatv.vertex.work.WorkExceptionHandler
import com.lovelycatv.vertex.work.base.AbstractWork
import com.lovelycatv.vertex.work.exception.WorkCoroutineScopeAwaitTimeoutException
import com.lovelycatv.vertex.work.exception.WorkCoroutineScopeNotInitializedException
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

class WorkCoroutineScope(
    private val context: CoroutineContext = Dispatchers.IO,
    var exceptionHandler: WorkExceptionHandler? = null
) : CoroutineScope {
    private val job = Job()

    private val startedJobs = mutableMapOf<AbstractWork, Job>()

    override val coroutineContext: CoroutineContext
        get() = context + job

    private var expectedJobs = 0

    private fun checkAvailability() {
        if (this.expectedJobs == 0) {
            throw WorkCoroutineScopeNotInitializedException()
        }
    }

    fun launchTask(identifier: AbstractWork, context: CoroutineContext = EmptyCoroutineContext, task: suspend () -> Unit): Job {
        this.checkAvailability()
        val newJob = runCoroutine(this, context) {
            try {
                task()
            } catch (e: Exception) {
                this.exceptionHandler?.invoke(e)
            }
        }
        startedJobs[identifier] = newJob
        return newJob
    }

    fun <R> launchTaskAsync(identifier: AbstractWork, context: CoroutineContext = EmptyCoroutineContext, task: suspend () -> R): Deferred<R?> {
        this.checkAvailability()
        val newJob = runCoroutineAsync(this, context) {
            try {
                task()
            } catch (e: Exception) {
                this.exceptionHandler?.invoke(e)
                null
            }
        }
        startedJobs[identifier] = newJob
        return newJob
    }

    fun cancelAllTasks(reason: String = "") {
        getActiveJobs().forEach {
            it.key.stopWork(reason)
        }
    }

    fun getStartedJobsMap() = this.startedJobs

    fun getActiveJobs() = this.getStartedJobsMap().filter { it.value.isActive }

    fun getInactiveJobs() = this.getStartedJobsMap().filter { !it.value.isActive }

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