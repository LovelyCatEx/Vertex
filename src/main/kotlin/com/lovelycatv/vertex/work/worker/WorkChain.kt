package com.lovelycatv.vertex.work.worker

import com.lovelycatv.vertex.work.data.InputWorkDataMerger
import com.lovelycatv.vertex.work.data.OverridingInputDataMerger
import java.util.UUID

/**
 * @author lovelycat
 * @since 2024-10-27 21:31
 * @version 1.0
 */
class WorkChain(val blocks: List<Block>) {
    val chainId = UUID.randomUUID().toString()

    fun getWorksCount(): Int = getAllWorks().size

    fun getAllWorks() = blocks.flatMap { it.works }

    data class Block(
        val works: List<WrappedWorker>,
        val isParallel: Boolean,
        val parallelInBound: Boolean,
        val inputWorkDataMerger: InputWorkDataMerger = OverridingInputDataMerger()
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

        fun transmit(inputWorkDataMerger: InputWorkDataMerger): Builder {
            val lastBlock = blocks[blocks.lastIndex]

            // Parallel Block is not supported
            if (lastBlock.isParallel && !lastBlock.parallelInBound) {
                throw IllegalStateException("Parallel Block does not support transmitting data to next Block")
            }

            blocks[blocks.lastIndex] = lastBlock.copy(
                inputWorkDataMerger = inputWorkDataMerger
            )

            return this
        }

        fun build(): WorkChain {
            return WorkChain(blocks)
        }
    }
}