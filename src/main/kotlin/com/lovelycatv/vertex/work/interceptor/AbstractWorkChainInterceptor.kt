package com.lovelycatv.vertex.work.interceptor

import com.lovelycatv.vertex.work.WorkChain
import com.lovelycatv.vertex.work.worker.WrappedWorker

/**
 * @author lovelycat
 * @since 2024-10-27 23:36
 * @version 1.0
 */
abstract class AbstractWorkChainInterceptor {
    abstract fun beforeBlockStarted(blockIndex: Int, block: WorkChain.Block)

    abstract fun beforeWorkStarted(blockIndex: Int, block: WorkChain.Block, work: WrappedWorker)

    abstract fun onBlockInterrupted(blockIndex: Int, block: WorkChain.Block, producer: WrappedWorker)

    abstract fun onChainInterrupted(blockIndex: Int, block: WorkChain.Block, producer: WrappedWorker)
}