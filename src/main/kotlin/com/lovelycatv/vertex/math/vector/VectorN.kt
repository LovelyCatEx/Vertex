package com.lovelycatv.vertex.math.vector

import com.lovelycatv.vertex.extension.dot
import kotlin.math.sqrt

/**
 * @author lovelycat
 * @since 2024-10-25 12:49
 * @version 1.0
 */
open class VectorN(
    private val n: Int,
    private var vector: DoubleArray = DoubleArray(n) { 0.0 }
) {
    fun getVectorValues() = this.vector.clone()

    fun dot(other: VectorN): Double {
        require(this.size == other.size)
        return this.vector dot other.vector
    }

    fun length(): Double {
        return sqrt(this.vector.sumOf { it * it })
    }

    fun identity(): VectorN {
        return this.div(this.length())
    }

    fun identify() {
        this.divAssign(this.length())
    }

    val size get() = this.vector.size

    operator fun plus(other: VectorN): VectorN {
        val r = DoubleArray(n)
        this.vector.forEachIndexed { index, d ->
            r[index] = d + other.vector[index]
        }
        return VectorN(this.n, r)
    }

    operator fun plusAssign(other: VectorN) {
        this.vector = this.plus(other).vector
    }

    operator fun minus(other: VectorN): VectorN {
        val r = DoubleArray(n)
        this.vector.forEachIndexed { index, d ->
            r[index] = d - other.vector[index]
        }
        return VectorN(this.n, r)
    }

    operator fun minusAssign(other: VectorN) {
        this.vector = this.minus(other).vector
    }

    operator fun times(other: Double): VectorN {
        val r = DoubleArray(n)
        this.vector.forEachIndexed { index, d ->
            r[index] = d * other
        }
        return VectorN(n, r)
    }

    operator fun timesAssign(v: Double) {
        this.vector = this.times(v).vector
    }

    operator fun div(other: Double): VectorN {
        if (other == 0.0) throw ArithmeticException("Division by zero")
        return this.times(1.0 / other)
    }

    operator fun divAssign(other: Double) {
        this.vector = this.div(other).vector
    }

    operator fun get(n: Int): Double {
        if (n < 0 || n >= this.vector.size) {
            throw IndexOutOfBoundsException("Index out of bounds")
        }
        return this.vector[n]
    }

    operator fun set(n: Int, value: Double) {
        if (n < 0 || n >= this.vector.size) {
            throw IndexOutOfBoundsException("Index out of bounds")
        }
        this.vector[n] = value
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as VectorN

        return vector.contentEquals(other.vector)
    }

    override fun hashCode(): Int {
        return vector.contentHashCode()
    }

    override fun toString(): String {
        return this.vector.map { it }.toString()
    }
}