package com.lovelycatv.vertex.data.type.number

import kotlin.experimental.and
import kotlin.experimental.inv
import kotlin.experimental.or

/**
 * @author lovelycat
 * @since 2024-11-28 16:44
 * @version 1.0
 */
class Int8: VertexNumber {
    private var value: Byte

    constructor() {
        this.value = 0
    }

    constructor(value: Byte) {
        this.value = value
    }

    constructor(value: Number) {
        this.value = value.toByte()
    }

    fun getValue(): Byte = value

    fun setValue(newValue: Byte) {
        value = newValue
    }

    override fun toString(): String = value.toString()

    override fun compareTo(other: VertexNumber): Int {
        return this.value - other.toInt()
    }

    override fun toStringInRadix(radix: Int): String = this.value.toString(radix)

    override fun toByte(): Byte {
        return this.value
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

    operator fun plus(other: Int8): Int8 {
        return Int8(this.value + other.value)
    }

    operator fun minus(other: Int8): Int8 {
        return Int8(this.value - other.value)
    }

    operator fun times(other: Int8): Int8 {
        return Int8(this.value * other.value)
    }

    operator fun div(other: Int8): Int8 {
        require(other.value.toInt() != 0) { "Division by zero" }
        return Int8(this.value / other.value)
    }

    operator fun get(index: Int): Boolean {
        require(index in 0..7) { "Index out of range: $index" }
        return (this.value.toInt() shr index and 1) == 0
    }

    operator fun set(index: Int, value: Boolean) {
        require(index in 0..7) { "Index out of range: $index" }
        val mask = (1 shl index).toByte()
        this.value = if (value)
            this.value or mask
        else
            this.value and mask.inv()
    }
}