package com.lovelycatv.vertex.collection.array

/**
 * @author lovelycat
 * @since 2024-11-28 16:28
 * @version 1.0
 */
class VLongArray(vararg initial: Long) : VNumberArray<Long>(*initial.toTypedArray()) {
    val longElements: LongArray get() = super.elements.toLongArray()

    override fun toVULongArray(): VULongArray {
        return super.elements.map { it.toULong() }.toVULongArray()
    }

    override fun toVUIntArray(): VUIntArray {
        return super.elements.map { it.toUInt() }.toVUIntArray()
    }

    override fun toVUShortArray(): VUShortArray {
        return super.elements.map { it.toUShort() }.toVUShortArray()
    }

    override fun toVUByteArray(): VUByteArray {
        return super.elements.map { it.toUByte() }.toVUByteArray()
    }
}