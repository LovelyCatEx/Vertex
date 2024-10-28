package com.lovelycatv.vertex.math.vector

/**
 * @author lovelycat
 * @since 2024-10-25 12:49
 * @version 1.0
 */
data class Vector2(val initialX: Number = 0.0, val initialY: Number = 0.0) : VectorN(2) {
    var x: Double
        get() = super.get(0)
        set(value) = super.set(0, value)
    var y: Double
        get() = super.get(1)
        set(value) = super.set(1, value)

    init {
        x = initialX.toDouble()
        y = initialY.toDouble()
    }
}