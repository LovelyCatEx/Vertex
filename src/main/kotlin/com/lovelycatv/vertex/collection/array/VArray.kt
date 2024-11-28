package com.lovelycatv.vertex.collection.array

/**
 * @author lovelycat
 * @since 2024-11-28 16:21
 * @version 1.0
 */
open class VArray<E> (vararg initial: E) {
    @Suppress("UNCHECKED_CAST")
    private val array: Array<E> = initial as Array<E>

    val elements: Array<E> get() = this.array

    val length: Int get() = this.array.size

    operator fun get(index: Int): E = this.array[index]

    operator fun set(index: Int, value: E) {
        this.array[index] = value
    }
}