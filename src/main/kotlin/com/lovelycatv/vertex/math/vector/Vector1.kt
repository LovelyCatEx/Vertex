package com.lovelycatv.vertex.math.vector

/**
 * @author lovelycat
 * @since 2024-10-25 12:49
 * @version 1.0
 */
class Vector1(initialX: Number = 0.0) : VectorN(1) {
    var x: Double
        get() = super.get(0)
        set(value) = super.set(0, value)

    init {
        x = initialX.toDouble()
    }
}