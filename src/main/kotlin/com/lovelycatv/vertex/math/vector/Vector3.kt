package com.lovelycatv.vertex.math.vector

/**
 * @author lovelycat
 * @since 2024-10-25 12:49
 * @version 1.0
 */
data class Vector3(
    val initialX: Number = 0.0,
    val initialY: Number = 0.0,
    val initialZ: Number = 0.0
) : VectorN(3) {
    var x: Double
        get() = super.get(0)
        set(value) = super.set(0, value)
    var y: Double
        get() = super.get(1)
        set(value) = super.set(1, value)
    var z: Double
        get() = super.get(2)
        set(value) = super.set(2, value)

    init {
        x = initialX.toDouble()
        y = initialY.toDouble()
        z = initialZ.toDouble()
    }
}