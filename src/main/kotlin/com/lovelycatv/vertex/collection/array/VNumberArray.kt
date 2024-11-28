package com.lovelycatv.vertex.collection.array

/**
 * A special array for number types.
 * For the sake of the implementation of Number and UNumber in kotlin is different,
 * The [Number] could not be converted to a UNumber directly.
 *
 * @author lovelycat
 * @since 2024-11-28 22:15
 * @version 1.0
 */
abstract class VNumberArray<E: Number>(vararg initial: E) : VArray<E>(*initial) {
    fun toVDoubleArray(): VDoubleArray {
        return this.elements.map { it.toDouble() }.toVDoubleArray()
    }

    fun toVFloatArray(): VFloatArray {
        return this.elements.map { it.toFloat() }.toVFloatArray()
    }

    fun toVLongArray(): VLongArray {
        return this.elements.map { it.toLong() }.toVLongArray()
    }

    abstract fun toVULongArray(): VULongArray

    fun toVIntArray(): VIntArray {
        return this.elements.map { it.toInt() }.toVIntArray()
    }

    abstract fun toVUIntArray(): VUIntArray

    fun toVShortArray(): VShortArray {
        return this.elements.map { it.toShort() }.toVShortArray()
    }

    abstract fun toVUShortArray(): VUShortArray

    fun toVByteArray(): VByteArray {
        return this.elements.map { it.toByte() }.toVByteArray()
    }

    abstract fun toVUByteArray(): VUByteArray

    fun toVStringArray(): VStringArray {
        return VStringArray(*this.elements.map { it.toString() }.toTypedArray())
    }
}