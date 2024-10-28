package com.lovelycatv.vertex.work.base

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
    val workId: String,
    val inputData: WorkData = WorkData.build(),
    private val throwException: Boolean = true,
    private val sideCoroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    private var lastStartedTimestamp = 0L
    private var runningJob: Job? = null
    private val _workResultLogs = MutableStateFlow(mutableListOf<WorkResult>())
    val workResultLogs: Flow<MutableList<WorkResult>> get() = this._workResultLogs

    init {
        this.postWorkResult(WorkResult(WorkState.INITIALIZED))
    }

    suspend fun startWork() {
        val currentState = this.getCurrentState()
        if (currentState != WorkState.RUNNING && currentState != WorkState.STEP_COMPLETED) {
            try {
                this.lastStartedTimestamp = System.currentTimeMillis()
                // Set initial running state
                this@AbstractWork.postStepStarted()
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

    fun stopWork(reason: String = "", output: WorkData = WorkData.build(), checkDelay: Long = 10) {
        runningJob?.let { currentJob ->
            sideCoroutineScope.launch {
                while (currentJob.isActive) {
                    if (this@AbstractWork.getCurrentState() == WorkState.STEP_COMPLETED) {
                        currentJob.cancel(reason)
                        break
                    }
                    delay(checkDelay)
                }
                postWorkResult(WorkResult.stopped(reason, output))
            }
        } ?: postWorkResult(WorkResult.stopped(reason, output))
    }

    fun forceStopWork(reason: String = "", output: WorkData = WorkData.build()) {
        if (this.getCurrentState() == WorkState.RUNNING || this.getCurrentState() == WorkState.STEP_COMPLETED) {
            runningJob?.cancel(reason)
            postWorkResult(WorkResult.stopped(reason, output))
        }
    }

    protected fun postStepStarted(output: WorkData = WorkData.build()) {
        postWorkResult(WorkResult.running(output))
    }

    protected fun postStepCompleted(output: WorkData = WorkData.build()) {
        postWorkResult(WorkResult.stepCompleted(output))
    }

    protected abstract suspend fun doWork(inputData: WorkData): WorkResult

    private fun postWorkResult(workResult: WorkResult) {
        this._workResultLogs.value = this._workResultLogs.value.toMutableList().apply { this.add(workResult) }
    }

    fun getCurrentWorkResult(): WorkResult = this._workResultLogs.value.last()

    fun getCurrentState(): WorkState = this.getCurrentWorkResult().state
}