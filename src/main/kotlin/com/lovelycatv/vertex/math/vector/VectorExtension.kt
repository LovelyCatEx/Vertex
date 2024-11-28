package com.lovelycatv.vertex.math.vector

/**
 * @author lovelycat
 * @since 2024-10-27 18:45
 * @version 1.0
 */
class VectorExtension private constructor()

fun DoubleArray.toVector(): VectorN {
    return VectorN(this.size, this)
}

fun DoubleArray.toVector1(): Vector1 {
    return Vector1(this[0])
}

fun DoubleArray.toVector2(): Vector2 {
    return Vector2(this[0], this[1])
}

fun DoubleArray.toVector3(): Vector3 {
    return Vector3(this[0], this[1], this[2])
}

fun DoubleArray.toVector4(): Vector4 {
    return Vector4(this[0], this[1], this[2], this[3])
}

fun Array<out VectorN>.avgCenter(): VectorN {
    require(this.isNotEmpty())
    require(this.map { it.size }.toSet().size == 1)
    val r = DoubleArray(this[0].size)
    this.forEach {
        for (i in 0..<it.size) {
            r[i] += it[i]
        }
    }
    return VectorN(r.size, r) / this.size.toDouble()
}

fun <V: VectorN> V.reduceDimension(to: Int): VectorN {
    if (to >= this.size) {
        return this
    }
    return VectorN(to, this.getVectorValues().copyOfRange(0, to))
}

fun <V: VectorN> V.riseDimension(to: Int): VectorN {
    if (to <= this.size) {
        return this
    }
    val vector = DoubleArray(to) { 0.0 }
    this.getVectorValues().forEachIndexed { index, d -> vector[index] = d }
    return VectorN(to, vector)
}