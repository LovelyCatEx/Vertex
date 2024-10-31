package com.lovelycatv.vertex.work.base

import com.lovelycatv.vertex.work.RetryIntervalSupplier
import com.lovelycatv.vertex.work.data.WorkData
import com.lovelycatv.vertex.work.WorkFailureStrategy
import com.lovelycatv.vertex.work.WorkRetryStrategy
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

/**
 * @author lovelycat
 * @since 2024-10-31 18:50
 * @version 1.0
 */
class WrappedWorker(
    private val workerId: String,
    private val originalWorker: AbstractWorker,
    private val retryStrategy: WorkRetryStrategy,
    private val failureStrategy: WorkFailureStrategy
) {
    fun getWorkerId() = this.workerId

    fun getWorker() = this.originalWorker

    fun getRetryStrategy() = this.retryStrategy

    fun getFailureStrategy() = this.failureStrategy

    class Builder<W: AbstractWorker>(
        private val workerId: String,
        private val workerClazz: KClass<W>
    ) {
        private var workName = ""
        private var workData = WorkData.build()

        private var retryStrategy = WorkRetryStrategy(
            type = WorkRetryStrategy.Type.NO_RETRY,
            retryInterval = { 0 },
            maxRetryTimes = 0
        )

        private var failureStrategy = WorkFailureStrategy.IGNORE

        fun workName(name: String): Builder<W> {
            this.workName = name
            return this
        }

        fun inputData(vararg pairs: Pair<String, Any?>): Builder<W> {
            this.workData = WorkData.build(*pairs)
            return this
        }

        fun retry(maxRetryTimes: Int, retryInterval: RetryIntervalSupplier): Builder<W> {
            this.retryStrategy = WorkRetryStrategy(
                type = WorkRetryStrategy.Type.RETRY,
                retryInterval = retryInterval,
                maxRetryTimes = maxRetryTimes
            )
            return this
        }

        fun noRetry(): Builder<W> {
            this.retryStrategy = WorkRetryStrategy(
                type = WorkRetryStrategy.Type.NO_RETRY,
                retryInterval = { 0 },
                maxRetryTimes = 0
            )
            return this
        }

        fun ignoreFailure(): Builder<W> {
            this.failureStrategy = WorkFailureStrategy.IGNORE
            return this
        }

        fun interruptBlockWhenFailure(): Builder<W> {
            this.failureStrategy = WorkFailureStrategy.INTERRUPT_BLOCK
            return this
        }

        fun interruptChainWhenFailure(): Builder<W> {
            this.failureStrategy = WorkFailureStrategy.INTERRUPT_CHAIN
            return this
        }

        fun build(): WrappedWorker {
            return WrappedWorker(
                workerId = this.workerId,
                originalWorker = workerClazz.primaryConstructor!!.call(this.workName, this.workData),
                retryStrategy = this.retryStrategy,
                failureStrategy = this.failureStrategy
            )
        }

    }
}