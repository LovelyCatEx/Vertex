package com.lovelycatv.vertex.math.linear

import kotlin.random.Random

/**
 * @author lovelycat
 * @since 2024-10-24 13:05
 * @version 1.0
 */
class FloatMatrixExtension private constructor()

fun filledFloatArray(n: Int, default: Float = 0f): FloatArray = FloatArray(n) { default }

fun zeroFloatMatrix(n: Int): FloatMatrix {
    return filledFloatMatrix(n, n, 0f)
}

fun filledFloatMatrix(m: Int, n: Int, filledValue: Float = 0f): FloatMatrix {
    return floatMatrix(m, n) { _, _ -> filledValue }
}

fun randomFloatMatrix(m: Int, n: Int, range: ClosedRange<Float> = (0f..1f)): FloatMatrix {
    val a = range.start.toDouble()
    val b = range.endInclusive.toDouble()
    return floatMatrix(m, n) { _, _ -> Random.nextDouble(a, b).toFloat() }
}

fun floatMatrix(m: Int, n: Int, elementProvider: () -> Float): FloatMatrix {
    return floatMatrix(m, n) { _, _ -> elementProvider.invoke() }
}

fun floatMatrix(m: Int, n: Int, elementProvider: ((i: Int, j: Int) -> Float)? = null): FloatMatrix {
    return FloatMatrix(
        (0..<m).map {
            rowIndex -> FloatArray(n) { colIndex ->
                elementProvider?.invoke(rowIndex, colIndex) ?: 0f
            }
        }
    )
}

fun identityFloatMatrix(n: Int): FloatMatrix {
    return FloatMatrix(
        (0..<n).map {
            val t = filledFloatArray(n)
            t[it] = 1f
            t
        }
    )
}

fun FloatMatrix.toDoubleMatrix(): DoubleMatrix {
    return DoubleMatrix(
        this.rows.map { floatArray ->
            DoubleArray(floatArray.size) {
                floatArray[it].toDouble()
            }
        }
    )
}

fun FloatArray.toMatrix(): FloatMatrix = FloatMatrix(this)

infix fun FloatArray.dot(other: FloatArray): Float {
    require(this.size == other.size)
    var sum = 0f
    for (i in this.indices) {
        sum += this[i] * other[i]
    }
    return sum
}