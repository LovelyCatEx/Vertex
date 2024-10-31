package com.lovelycatv.vertex.work.base

import com.lovelycatv.vertex.work.WorkData
import com.lovelycatv.vertex.work.WorkResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel

/**
 * @author lovelycat
 * @since 2024-10-27 20:09
 * @version 1.0
 */
sealed class AbstractWork(
    val workName: String,
    val inputData: WorkData
) {
    private var lastStartedTimestamp = 0L

    open suspend fun startWork(): WorkResult {
        this.lastStartedTimestamp = System.currentTimeMillis()
        return doWork(inputData)
    }

    open suspend fun stopWork(job: Job, reason: String) {
        job.cancel(reason)
    }

    open fun forceStopWork(job: Job, reason: String) {
        job.cancel(reason)
    }

    protected abstract suspend fun doWork(inputData: WorkData): WorkResult
}