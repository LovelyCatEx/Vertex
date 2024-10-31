package com.lovelycatv.vertex.work.extension

import com.lovelycatv.vertex.work.base.AbstractWorker
import com.lovelycatv.vertex.work.base.WrappedWorker
import java.util.UUID

/**
 * @author lovelycat
 * @since 2024-10-31 18:57
 * @version 1.0
 */
class WorkerBuilderExtension private constructor()

inline fun <reified W: AbstractWorker> WorkerBuilder(): WrappedWorker.Builder<W> {
    return WrappedWorker.Builder(UUID.randomUUID().toString(), W::class)
}