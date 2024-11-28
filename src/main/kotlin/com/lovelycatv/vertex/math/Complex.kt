package com.lovelycatv.vertex.math

/**
 * @author lovelycat
 * @since 2024-10-24 13:50
 * @version 1.0
 */
class Complex(val i: Double, val j: Double) {
    operator fun plus(other: Complex): Complex {
        return (this.i + other.i).complex(this.j + other.j)
    }

    operator fun minus(other: Complex): Complex {
        return (this.i - other.i).complex(this.j - other.j)
    }

    operator fun times(other: Complex): Complex {
        return (this.i * other.i - this.j * other.j).complex(this.i * other.j + this.j * other.i)
    }

    override fun toString(): String {
        return "$i + ${j}i"
    }
}