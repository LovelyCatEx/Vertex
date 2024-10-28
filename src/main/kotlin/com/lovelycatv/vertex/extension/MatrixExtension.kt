package com.lovelycatv.vertex.extension

import com.lovelycatv.vertex.math.linear.Matrix

/**
 * @author lovelycat
 * @since 2024-10-24 13:05
 * @version 1.0
 */
class MatrixExtension private constructor()

fun emptyDoubleArray(n: Int, default: Double = 0.0): DoubleArray = DoubleArray(n) { default }

fun identityMatrix(n: Int): Matrix {
   return Matrix(
       (0..<n).map {
           val t = emptyDoubleArray(n)
           t[it] = 1.0
           t
       }
   )
}

fun filledMatrix(m: Int, n: Int, filledValue: Double = 0.0): Matrix {
    return randomMatrix(m, n) { _, _ -> filledValue }
}

fun randomMatrix(m: Int, n: Int, elementProvider: () -> Double): Matrix {
    return randomMatrix(m, n) { _, _ -> elementProvider.invoke() }
}

fun randomMatrix(m: Int, n: Int, elementProvider: ((i: Int, j: Int) -> Double)? = null): Matrix {
    return Matrix(
        (0..<m).map { rowIndex -> DoubleArray(n) { colIndex -> elementProvider?.invoke(rowIndex, colIndex) ?: 0.0 } }
    )
}

fun zeroMatrix(n: Int): Matrix {
    return filledMatrix(n, n, 0.0)
}

fun DoubleArray.toMatrix(): Matrix = Matrix(this)

infix fun DoubleArray.dot(other: DoubleArray): Double {
    require(this.size == other.size)
    var sum = 0.0
    for (i in this.indices) {
        sum += this[i] * other[i]
    }
    return sum
}