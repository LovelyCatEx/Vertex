package com.lovelycatv.vertex.type.number

/**
 * For unsigned custom number types
 *
 * @author lovelycat
 * @since 2024-11-28 16:49
 * @version 1.0
 */
abstract class VertexUNumber : VertexNumber() {
    abstract fun toUByte(): UByte

    abstract fun toUShort(): UShort

    abstract fun toUInt(): UInt

    abstract fun toULong(): ULong
}