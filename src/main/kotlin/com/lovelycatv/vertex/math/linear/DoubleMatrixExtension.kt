package com.lovelycatv.vertex.math.linear

import kotlin.random.Random

/**
 * @author lovelycat
 * @since 2024-10-24 13:05
 * @version 1.0
 */
class DoubleMatrixExtension private constructor()

fun filledDoubleArray(n: Int, default: Double = 0.0): DoubleArray = DoubleArray(n) { default }

fun zeroDoubleMatrix(n: Int): DoubleMatrix {
    return filledDoubleMatrix(n, n, 0.0)
}

fun filledDoubleMatrix(m: Int, n: Int, filledValue: Double = 0.0): DoubleMatrix {
    return doubleMatrix(m, n) { _, _ -> filledValue }
}

fun randomDoubleMatrix(m: Int, n: Int, range: ClosedRange<Double> = (.0..1.0)): DoubleMatrix {
    return doubleMatrix(m, n) { _, _ -> Random.nextDouble(range.start, range.endInclusive) }
}

fun doubleMatrix(m: Int, n: Int, elementProvider: () -> Double): DoubleMatrix {
    return doubleMatrix(m, n) { _, _ -> elementProvider.invoke() }
}

fun doubleMatrix(m: Int, n: Int, elementProvider: ((i: Int, j: Int) -> Double)? = null): DoubleMatrix {
    return DoubleMatrix(
        (0..<m).map {
            rowIndex -> DoubleArray(n) { colIndex ->
                elementProvider?.invoke(rowIndex, colIndex) ?: 0.0
            }
        }
    )
}

fun identityDoubleMatrix(n: Int): DoubleMatrix {
    return DoubleMatrix(
        (0..<n).map {
            val t = filledDoubleArray(n)
            t[it] = 1.0
            t
        }
    )
}

fun DoubleMatrix.toFloatMatrix(): FloatMatrix {
    return FloatMatrix(
        this.rows.map { doubleArray ->
            FloatArray(doubleArray.size) {
                doubleArray[it].toFloat()
            }
        }
    )
}

fun DoubleArray.toMatrix(): DoubleMatrix = DoubleMatrix(this)

infix fun DoubleArray.dot(other: DoubleArray): Double {
    require(this.size == other.size)
    var sum = 0.0
    for (i in this.indices) {
        sum += this[i] * other[i]
    }
    return sum
}

fun main() {
    val a = doubleMatrix(100, 100)
    println(a)
}