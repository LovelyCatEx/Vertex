package com.lovelycatv.vertex.data.type.number

/**
 * @author lovelycat
 * @since 2024-11-28 16:54
 * @version 1.0
 */
class VertexNumberExtensions private constructor()

fun Short.toInt8(): Int8 = Int8(this.toByte())

fun Int.toInt8(): Int8 = Int8(this.toByte())

fun Long.toInt8(): Int8 = Int8(this.toByte())

fun UShort.toInt8(): Int8 = Int8(this.toByte())

fun UInt.toInt8(): Int8 = Int8(this.toByte())

fun ULong.toInt8(): Int8 = Int8(this.toByte())

fun Float.toInt8(): Int8 = Int8(this.toInt().toByte())

fun Double.toInt8(): Int8 = Int8(this.toInt().toByte())

fun Short.toInt4(): Int4 = Int4(this.toByte())

fun Int.toInt4(): Int4 = Int4(this.toByte())

fun Long.toInt4(): Int4 = Int4(this.toByte())

fun UShort.toInt4(): Int4 = Int4(this.toByte())

fun UInt.toInt4(): Int4 = Int4(this.toByte())

fun ULong.toInt4(): Int4 = Int4(this.toByte())

fun Float.toInt4(): Int4 = Int4(this.toInt().toByte())

fun Double.toInt4(): Int4 = Int4(this.toInt().toByte())

fun Int8.toUInt8(): UInt8 = UInt8(this.getValue())

fun Int8.toInt4(): Int4 = Int4(this.getValue())

fun Int8.toInt(): Int = this.getValue().toInt()

fun Int4.toUInt4(): UInt4 = UInt4(this.getValue())

fun Int4.toInt8(): Int8 = Int8(this.getValue())

fun Int4.toInt(): Int = this.getValue().toInt()

fun UInt8.toUInt4(): UInt4 = UInt4(this.getValue().toByte())

fun UInt4.toUInt8(): UInt8 = UInt8(this.getValue())
