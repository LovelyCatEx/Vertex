package com.lovelycatv.vertex.work.base

import com.lovelycatv.vertex.work.WorkChain

/**
 * @author lovelycat
 * @since 2024-10-27 23:36
 * @version 1.0
 */
abstract class AbstractWorkChainInterceptor {
    abstract fun beforeBlockStarted(blockIndex: Int, block: WorkChain.Block)

    abstract fun beforeWorkStarted(blockIndex: Int, block: WorkChain.Block, workIndex: Int, work: AbstractWork)
}