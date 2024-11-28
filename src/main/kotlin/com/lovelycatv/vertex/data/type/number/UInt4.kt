package com.lovelycatv.vertex.data.type.number

import kotlin.experimental.and
import kotlin.experimental.inv
import kotlin.experimental.or

/**
 * @author lovelycat
 * @since 2024-11-28 16:44
 * @version 1.0
 */
class UInt4: VertexNumber {
    private var value: Byte

    constructor() {
        this.value = 0
    }

    constructor(value: Byte) {
        this.value = normalize(value)
    }

    constructor(value: Number) {
        this.value = normalize(value.toByte())
    }

    fun getValue(): Byte = value

    fun setValue(newValue: Byte) {
        value = normalize(newValue)
    }

    override fun toString(): String = value.toString()

    override fun toStringInRadix(radix: Int): String = this.value.toString(radix)

    operator fun plus(other: UInt4): UInt4 {
        return UInt4(this.value + other.value)
    }

    operator fun minus(other: UInt4): UInt4 {
        return UInt4(this.value - other.value)
    }

    operator fun times(other: UInt4): UInt4 {
        return UInt4(this.value * other.value)
    }

    operator fun div(other: UInt4): UInt4 {
        require(other.value.toInt() != 0) { "Division by zero" }
        return UInt4(this.value / other.value)
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

    private fun normalize(value: Byte): Byte {
        return ((value.toUByte().toUInt() shl 4).toUByte().toUInt() shr 4).toByte()
    }
}