package com.lovelycatv.vertex.data.type.number

/**
 * Designed for the custom number types
 *
 * @author lovelycat
 * @since 2024-11-28 16:49
 * @version 1.0
 */
abstract class VertexNumber : Number(), Comparable<VertexNumber> {
    abstract fun toStringInRadix(radix: Int): String

    /**
     * Parse the number to binary string
     *
     * @param real If true, the binary string will be displayed as the actual bits stored in memory without negative(-) sign
     * @return Binary string
     */
    fun toBinaryString(real: Boolean = false): String {
        return if (real)
            this.toStringInRadix(2).run {
                if (this.contains("-")) {
                    (this.replace("-", "").toUByte(2).inv() + 1u).toString(2).drop(4)
                } else {
                    this
                }
            }
        else
            this.toStringInRadix(2)
    }

    fun toOctString(): String = this.toStringInRadix(8)

    fun toHexString(): String = this.toStringInRadix(16)
}