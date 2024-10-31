package com.lovelycatv.vertex.work.interceptor

import com.lovelycatv.vertex.work.WorkChain
import com.lovelycatv.vertex.work.base.WrappedWorker

/**
 * @author lovelycat
 * @since 2024-10-31 21:17
 * @version 1.0
 */
open class DefaultWorkChainInterceptor : AbstractWorkChainInterceptor() {
    override fun beforeBlockStarted(blockIndex: Int, block: WorkChain.Block) {}

    override fun beforeWorkStarted(blockIndex: Int, block: WorkChain.Block, work: WrappedWorker) {}

    override fun onBlockInterrupted(blockIndex: Int, block: WorkChain.Block, producer: WrappedWorker) {}

    override fun onChainInterrupted(blockIndex: Int, block: WorkChain.Block, producer: WrappedWorker) {}
}