package com.lovelycatv.vertex.work

import com.lovelycatv.vertex.work.base.WrappedWorker

/**
 * @author lovelycat
 * @since 2024-10-27 21:31
 * @version 1.0
 */
class WorkChain(val blocks: List<Block>) {
    fun getTotalWorks(): Int = blocks.flatMap { it.works }.size

    data class Block(
        val works: List<WrappedWorker>,
        val isParallel: Boolean,
        val parallelInBound: Boolean
    )

    class Builder {
        private val blocks = mutableListOf<Block>()

        fun sequence(vararg works: WrappedWorker): Builder {
            blocks.add(Block(works.toList(), isParallel = false, parallelInBound = true))
            return this
        }

        fun parallel(vararg works: WrappedWorker): Builder {
            blocks.add(Block(works.toList(), isParallel = true, parallelInBound = false))
            return this
        }

        fun parallelInBound(vararg works: WrappedWorker): Builder {
            blocks.add(Block(works.toList(), isParallel = true, parallelInBound = true))
            return this
        }

        fun build(): WorkChain {
            return WorkChain(blocks)
        }
    }
}