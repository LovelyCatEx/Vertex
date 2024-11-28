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
 * For Iterable & VArray
 */
inline fun <reified T> Array<T>.toVArray(): VArray<T> {
    return VArray(*this.toList().toTypedArray())
}

inline fun <reified T> Iterable<T>.toVArray(): VArray<T> {
    return VArray(*this.toList().toTypedArray())
}

inline fun <reified T> Collection<T>.toVArray(): VArray<T> {
    return VArray(*this.toTypedArray())
}

inline fun <reified T> VArray<T>.toArray(): Array<T> {
    return Array(this.length) { this[it] }
}

fun <T> VArray<T>.toIterable(): Iterable<T> {
    return this.elements.toList()
}

fun <T> VArray<T>.toList(): List<T> {
    return this.elements.toList()
}

fun Array<Double>.toVDoubleArray(): VDoubleArray {
    return VDoubleArray(*this.toDoubleArray())
}

fun Iterable<Double>.toVDoubleArray(): VDoubleArray {
    return VDoubleArray(*this.toList().toDoubleArray())
}

fun Collection<Double>.toVDoubleArray(): VDoubleArray {
    return VDoubleArray(*this.toDoubleArray())
}

fun VDoubleArray.toDoubleArray(): DoubleArray {
    return this.doubleElements
}

fun Array<Float>.toVFloatArray(): VFloatArray {
    return VFloatArray(*this.toFloatArray())
}

fun Iterable<Float>.toVFloatArray(): VFloatArray {
    return VFloatArray(*this.toList().toFloatArray())
}

fun Collection<Float>.toVFloatArray(): VFloatArray {
    return VFloatArray(*this.toFloatArray())
}

fun VFloatArray.toFloatArray(): FloatArray {
    return this.floatElements
}

fun Array<Long>.toVLongArray(): VLongArray {
    return VLongArray(*this.toLongArray())
}

fun Iterable<Long>.toVLongArray(): VLongArray {
    return VLongArray(*this.toList().toLongArray())
}

fun Collection<Long>.toVLongArray(): VLongArray {
    return VLongArray(*this.toLongArray())
}

fun VLongArray.toLongArray(): LongArray {
    return this.longElements
}

fun Array<Int>.toVIntArray(): VIntArray {
    return VIntArray(*this.toIntArray())
}

fun Iterable<Int>.toVIntArray(): VIntArray {
    return VIntArray(*this.toList().toIntArray())
}

fun Collection<Int>.toVIntArray(): VIntArray {
    return VIntArray(*this.toIntArray())
}

fun VIntArray.toIntArray(): IntArray {
    return this.intElements
}

fun Array<Short>.toVShortArray(): VShortArray {
    return VShortArray(*this.toShortArray())
}

fun Iterable<Short>.toVShortArray(): VShortArray {
    return VShortArray(*this.toList().toShortArray())
}

fun Collection<Short>.toVShortArray(): VShortArray {
    return VShortArray(*this.toShortArray())
}

fun VShortArray.toShortArray(): ShortArray {
    return this.shortElements
}

fun Array<Byte>.toVByteArray(): VByteArray {
    return VByteArray(*this.toByteArray())
}

fun Iterable<Byte>.toVByteArray(): VByteArray {
    return VByteArray(*this.toList().toByteArray())
}

fun Collection<Byte>.toVByteArray(): VByteArray {
    return VByteArray(*this.toByteArray())
}

fun VByteArray.toByteArray(): ByteArray {
    return this.byteElements
}

@OptIn(ExperimentalUnsignedTypes::class)
fun Array<ULong>.toVULongArray(): VULongArray {
    return VULongArray(*this.toULongArray())
}

@OptIn(ExperimentalUnsignedTypes::class)
fun Iterable<ULong>.toVULongArray(): VULongArray {
    return VULongArray(*this.toList().toULongArray())
}

@OptIn(ExperimentalUnsignedTypes::class)
fun Collection<ULong>.toVULongArray(): VULongArray {
    return VULongArray(*this.toULongArray())
}

@OptIn(ExperimentalUnsignedTypes::class)
fun VULongArray.toULongArray(): ULongArray {
    return this.uLongElements
}

@OptIn(ExperimentalUnsignedTypes::class)
fun Array<UInt>.toVUIntArray(): VUIntArray {
    return VUIntArray(*this.toUIntArray())
}

@OptIn(ExperimentalUnsignedTypes::class)
fun Iterable<UInt>.toVUIntArray(): VUIntArray {
    return VUIntArray(*this.toList().toUIntArray())
}

@OptIn(ExperimentalUnsignedTypes::class)
fun Collection<UInt>.toVUIntArray(): VUIntArray {
    return VUIntArray(*this.toUIntArray())
}

@OptIn(ExperimentalUnsignedTypes::class)
fun VUIntArray.toUIntArray(): UIntArray {
    return this.uIntElements
}

@OptIn(ExperimentalUnsignedTypes::class)
fun Array<UShort>.toVUShortArray(): VUShortArray {
    return VUShortArray(*this.toUShortArray())
}

@OptIn(ExperimentalUnsignedTypes::class)
fun Iterable<UShort>.toVUShortArray(): VUShortArray {
    return VUShortArray(*this.toList().toUShortArray())
}

@OptIn(ExperimentalUnsignedTypes::class)
fun Collection<UShort>.toVUShortArray(): VUShortArray {
    return VUShortArray(*this.toUShortArray())
}

@OptIn(ExperimentalUnsignedTypes::class)
fun VUShortArray.toUShortArray(): UShortArray {
    return this.uShortElements
}

@OptIn(ExperimentalUnsignedTypes::class)
fun Array<UByte>.toVUByteArray(): VUByteArray {
    return VUByteArray(*this.toUByteArray())
}

@OptIn(ExperimentalUnsignedTypes::class)
fun Iterable<UByte>.toVUByteArray(): VUByteArray {
    return VUByteArray(*this.toList().toUByteArray())
}

@OptIn(ExperimentalUnsignedTypes::class)
fun Collection<UByte>.toVUByteArray(): VUByteArray {
    return VUByteArray(*this.toUByteArray())
}

@OptIn(ExperimentalUnsignedTypes::class)
fun VUByteArray.toUByteArray(): UByteArray {
    return this.uByteElements
}


/**
 * For VArray & Matrix
 */
typealias VMatrix = VArray<VDoubleArray>

fun VDoubleArray.toMatrix(): Matrix {
    return matrix(1, this.length) { _, j -> this[j] }
}

fun VMatrix.toMatrix(): Matrix {
    val maxLength = this.elements.maxOf { it.length }
    return matrix(this.length, maxLength) { i, j ->
        if (j > this[i].length - 1) .0 else this[i][j]
    }
}