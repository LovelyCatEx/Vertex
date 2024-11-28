package com.lovelycatv.vertex.collection.array

/**
 * @author lovelycat
 * @since 2024-11-28 16:28
 * @version 1.0
 */
@OptIn(ExperimentalUnsignedTypes::class)
class VULongArray(vararg initial: ULong) : VUNumberArray<ULong>(*initial.toTypedArray()) {
    val uLongElements: ULongArray get() = super.elements.toULongArray()

    override fun toVULongArray(): VULongArray {
        return super.elements.map { it }.toVULongArray()
    }

    override fun toVLongArray(): VLongArray {
        return super.elements.map { it.toLong() }.toVLongArray()
    }

    override fun toVUIntArray(): VUIntArray {
        return super.elements.map { it.toUInt() }.toVUIntArray()
    }

    override fun toVIntArray(): VIntArray {
        return super.elements.map { it.toInt() }.toVIntArray()
    }

    override fun toVUShortArray(): VUShortArray {
        return super.elements.map { it.toUShort() }.toVUShortArray()
    }

    override fun toVShortArray(): VShortArray {
        return super.elements.map { it.toShort() }.toVShortArray()
    }

    override fun toVUByteArray(): VUByteArray {
        return super.elements.map { it.toUByte() }.toVUByteArray()
    }

    override fun toVByteArray(): VByteArray {
        return super.elements.map { it.toByte() }.toVByteArray()
    }
}