package com.lovelycatv.vertex.data.type.number

/**
 * @author lovelycat
 * @since 2024-11-28 16:44
 * @version 1.0
 */
class UInt8: VertexUNumber {
    private var value: UByte

    constructor() {
        this.value = 0u
    }

    constructor(value: UByte) {
        this.value = value
    }

    constructor(value: Number) {
        this.value = value.toInt().toUByte()
    }

    fun getValue(): UByte = value

    fun setValue(newValue: UByte) {
        value = newValue
    }

    override fun toString(): String = value.toString()

    override fun compareTo(other: VertexNumber): Int {
        return this.toInt() - other.toInt()
    }

    override fun toUByte(): UByte {
        return this.value.toUByte()
    }

    override fun toUShort(): UShort {
        return this.value.toUShort()
    }

    override fun toUInt(): UInt {
        return this.value.toUInt()
    }

    override fun toULong(): ULong {
        return this.value.toULong()
    }

    override fun toStringInRadix(radix: Int): String = this.value.toString(radix)

    override fun toByte(): Byte {
        return this.value.toByte()
    }

    override fun toDouble(): Double {
        return this.value.toDouble()
    }

    override fun toFloat(): Float {
        return this.value.toFloat()
    }

    override fun toInt(): Int {
        return this.value.toInt()
    }

    override fun toLong(): Long {
        return this.value.toLong()
    }

    override fun toShort(): Short {
        return this.value.toShort()
    }

    operator fun plus(other: UInt8): UInt8 {
        return UInt8((this.value + other.value).toUByte())
    }

    operator fun minus(other: UInt8): UInt8 {
        return UInt8((this.value - other.value).toUByte())
    }

    operator fun times(other: UInt8): UInt8 {
        return UInt8((this.value * other.value).toUByte())
    }

    operator fun div(other: UInt8): UInt8 {
        require(other.value.toInt() != 0) { "Division by zero" }
        return UInt8((this.value / other.value).toUByte())
    }

    operator fun get(index: Int): Boolean {
        require(index in 0..7) { "Index out of range: $index" }
        return (this.value.toInt() shr index and 1) == 0
    }

    operator fun set(index: Int, value: Boolean) {
        require(index in 0..7) { "Index out of range: $index" }
        val mask = (1 shl index).toUByte()
        this.value = if (value)
            this.value or mask
        else
            this.value and mask.inv()
    }
}