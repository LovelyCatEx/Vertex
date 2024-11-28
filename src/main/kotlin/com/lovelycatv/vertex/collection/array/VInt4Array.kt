package com.lovelycatv.vertex.collection.array

import com.lovelycatv.vertex.data.type.number.Int4
import com.lovelycatv.vertex.data.type.number.toUInt4

/**
 * @author lovelycat
 * @since 2024-11-28 16:28
 * @version 1.0
 */
class VInt4Array(vararg initial: Int4) : VNumberArray<Int4>(*initial) {
    override fun toVULongArray(): VULongArray {
        return super.elements.map { it.toUInt4().toULong() }.toVULongArray()
    }

    override fun toVUIntArray(): VUIntArray {
        return super.elements.map { it.toUInt4().toUInt() }.toVUIntArray()
    }

    override fun toVUShortArray(): VUShortArray {
        return super.elements.map { it.toUInt4().toUShort() }.toVUShortArray()
    }

    override fun toVUByteArray(): VUByteArray {
        return super.elements.map { it.toUInt4().toUByte() }.toVUByteArray()
    }
}