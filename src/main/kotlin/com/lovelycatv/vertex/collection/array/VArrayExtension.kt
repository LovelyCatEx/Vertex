package com.lovelycatv.vertex.collection.array

import com.lovelycatv.vertex.data.type.number.*
import com.lovelycatv.vertex.math.linear.matrix
import com.lovelycatv.vertex.math.linear.Matrix

/**
 * @author lovelycat
 * @since 2024-11-28 18:30
 * @version 1.0
 */
class VArrayExtension private constructor()

fun <T> Array<T>.toVArray(): VArray<T> {
    return VArray(*this)
}

inline fun <reified T> VArray<T>.toArray(): Array<T> {
    return Array(this.length) { this[it] }
}

inline fun <reified E> vertexArray(size: Int, init: (Int) -> E): VArray<E> {
    return VArray(*Array(size) { init.invoke(it) })
}

fun vertexDoubleArray(size: Int, init: (Int) -> Double = { .0 }): VDoubleArray {
    return VDoubleArray(*DoubleArray(size) { init.invoke(it) })
}

fun vertexFloatArray(size: Int, init: (Int) -> Float = { 0f }): VFloatArray {
    return VFloatArray(*FloatArray(size) { init.invoke(it) })
}

fun vertexLongArray(size: Int, init: (Int) -> Long = { 0 }): VLongArray {
    return VLongArray(*LongArray(size) { init.invoke(it) })
}

fun vertexIntArray(size: Int, init: (Int) -> Int = { 0 }): VIntArray {
    return VIntArray(*IntArray(size) { init.invoke(it) })
}

fun vertexInt4Array(size: Int, init: (Int) -> Int4 = { Int4() }): VInt4Array {
    return VInt4Array(*Array(size) { init.invoke(it) })
}

fun vertexInt8Array(size: Int, init: (Int) -> Int8 = { Int8() }): VInt8Array {
    return VInt8Array(*Array(size) { init.invoke(it) })
}

fun vertexShortArray(size: Int, init: (Int) -> Short = { 0 }): VShortArray {
    return VShortArray(*ShortArray(size) { init.invoke(it) })
}

@OptIn(ExperimentalUnsignedTypes::class)
fun vertexULongArray(size: Int, init: (Int) -> ULong = { 0u }): VULongArray {
    return VULongArray(*ULongArray(size) { init.invoke(it) })
}

@OptIn(ExperimentalUnsignedTypes::class)
fun vertexUIntArray(size: Int, init: (Int) -> UInt = { 0u }): VUIntArray {
    return VUIntArray(*UIntArray(size) { init.invoke(it) })
}

fun vertexUInt4Array(size: Int, init: (Int) -> UInt4 = { UInt4() }): VUInt4Array {
    return VUInt4Array(*Array(size) { init.invoke(it) })
}

fun vertexUInt8Array(size: Int, init: (Int) -> UInt8 = { UInt8() }): VUInt8Array {
    return VUInt8Array(*Array(size) { init.invoke(it) })
}

@OptIn(ExperimentalUnsignedTypes::class)
fun vertexUShortArray(size: Int, init: (Int) -> UShort = { 0u }): VUShortArray {
    return VUShortArray(*UShortArray(size) { init.invoke(it) })
}

/**
 * For VArray & Matrix
 */
fun VDoubleArray.toMatrix(): Matrix {
    return matrix(1, this.length) { _, j -> this[j] }
}