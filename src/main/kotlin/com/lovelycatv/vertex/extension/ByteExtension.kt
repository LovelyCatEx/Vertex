package com.lovelycatv.vertex.extension

import kotlin.experimental.and
import kotlin.experimental.inv
import kotlin.experimental.or

/**
 * @author lovelycat
 * @since 2024-11-29 20:33
 * @version 1.0
 */
class ByteExtension private constructor()

operator fun Byte.get(index: Int): Boolean {
    require(index in 0..7) { "Index out of range: $index" }
    return (this.toInt() shr index and 1) == 0
}

fun Byte.set(index: Int, value: Boolean): Byte {
    require(index in 0..7) { "Index out of range: $index" }
    val mask = (1 shl index).toByte()
    return if (value)
        this or mask
    else
        this and mask.inv()
}