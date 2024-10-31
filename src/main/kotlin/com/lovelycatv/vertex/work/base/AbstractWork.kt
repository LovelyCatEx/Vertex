package com.lovelycatv.vertex.work.base

import com.lovelycatv.vertex.extension.runCoroutine
import com.lovelycatv.vertex.work.WorkData
import com.lovelycatv.vertex.work.WorkResult
import com.lovelycatv.vertex.work.WorkState
import com.lovelycatv.vertex.work.exception.WorkNotCompletedException
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * @author lovelycat
 * @since 2024-10-27 20:09
 * @version 1.0
 */
abstract class AbstractWork(
    val workName: String,
    val inputData: WorkData = WorkData.build(),
    private val throwException: Boolean = true
) {
    private var lastStartedTimestamp = 0L
    private val _workResultLogs = MutableStateFlow(mutableListOf<WorkResult>())
    val workResultLogs: Flow<MutableList<WorkResult>> get() = this._workResultLogs

    init {
        this.postWorkResult(WorkResult(WorkState.INITIALIZED))
    }

    /**
     * Protected Jobs
     */
    private val protectedCoroutineScope = CoroutineScope(Dispatchers.IO + Job())
    private val protectedJobs = mutableListOf<Job>()

    fun anyProtectJobsRunning(): Boolean {
        return protectedJobs.map { it.isActive }.toSet().run {
            this.size == 1 && this.iterator().next()
        }
    }

    fun cancelAllProtectedJobs(reason: String) {
        protectedJobs.filter { it.isActive }.forEach {
            it.cancel(reason)
        }
    }

    fun runInProtected(fx: suspend () -> Unit) {
        val job = runCoroutine(protectedCoroutineScope) {
            fx()
        }
        this.protectedJobs.add(job)
    }

    suspend fun waitForProtectedJobs() {
        while (this.anyProtectJobsRunning()) {
            delay(100)
        }
    }

    suspend fun startWork() {
        val currentState = this.getCurrentState()
        if (currentState != WorkState.RUNNING) {
            try {
                this.lastStartedTimestamp = System.currentTimeMillis()
                // Set initial running state
                this@AbstractWork.postWorkStarted()
                val finalResult = doWork(this@AbstractWork.inputData)
                this@AbstractWork.postWorkResult(finalResult)
            } catch (e: Exception) {
                this@AbstractWork.postWorkResult(WorkResult.error(e))
                if (throwException) {
                    // Throw the exception to the upper WorkCoroutineScope
                    throw e
                }
            }
        } else {
            throw WorkNotCompletedException(this)
        }
    }

    private fun postWorkStarted(output: WorkData = WorkData.build()) {
        postWorkResult(WorkResult.running(output))
    }

    protected abstract suspend fun doWork(inputData: WorkData): WorkResult

    private fun postWorkResult(workResult: WorkResult) {
        this._workResultLogs.value = this._workResultLogs.value.toMutableList().apply { this.add(workResult) }
    }

    fun getCurrentWorkResult(): WorkResult = this._workResultLogs.value.last()

    fun getCurrentState(): WorkState = this.getCurrentWorkResult().state
}