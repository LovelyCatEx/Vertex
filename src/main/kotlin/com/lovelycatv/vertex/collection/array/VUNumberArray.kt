package com.lovelycatv.vertex.collection.array

/**
 * A special array for UNumber.
 * For the sake of the implementation of Number and UNumber in kotlin is different,
 * This class could not extend VNumberArray<E> directly.
 *
 * @author lovelycat
 * @since 2024-11-28 22:15
 * @version 1.0
 */
abstract class VUNumberArray<E: Comparable<E>>(vararg initial: E) : VArray<E>(*initial) {
    abstract fun toVULongArray(): VULongArray

    abstract fun toVLongArray(): VLongArray

    abstract fun toVUIntArray(): VUIntArray

    abstract fun toVIntArray(): VIntArray

    abstract fun toVUShortArray(): VUShortArray

    abstract fun toVShortArray(): VShortArray

    abstract fun toVUByteArray(): VUByteArray

    abstract fun toVByteArray(): VByteArray
}