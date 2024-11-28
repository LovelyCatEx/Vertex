package com.lovelycatv.vertex.collection.array

import com.lovelycatv.vertex.data.type.number.Int8
import com.lovelycatv.vertex.data.type.number.toUInt8

/**
 * @author lovelycat
 * @since 2024-11-28 16:28
 * @version 1.0
 */
class VInt8Array(vararg initial: Int8) : VNumberArray<Int8>(*initial) {
    override fun toVULongArray(): VULongArray {
        return super.elements.map { it.toUInt8().toULong() }.toVULongArray()
    }

    override fun toVUIntArray(): VUIntArray {
        return super.elements.map { it.toUInt8().toUInt() }.toVUIntArray()
    }

    override fun toVUShortArray(): VUShortArray {
        return super.elements.map { it.toUInt8().toUShort() }.toVUShortArray()
    }

    override fun toVUByteArray(): VUByteArray {
        return super.elements.map { it.toUInt8().toUByte() }.toVUByteArray()
    }
}