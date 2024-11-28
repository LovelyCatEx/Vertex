package com.lovelycatv.vertex.math

/**
 * @author lovelycat
 * @since 2024-10-24 13:59
 * @version 1.0
 */
class MathExtension private constructor()

inline fun sigma(n: Long, k: Long = 1, fx: (n: Long) -> Double): Double {
    var sum = fx(k)
    for (i in k + 1..n) {
        sum += fx(i)
    }
    return sum
}

inline fun pi(n: Long, k: Long = 1, fx: (n: Long) -> Double): Double {
    var mul = fx(k)
    for (i in k + 1..n) {
        mul *= fx(i)
    }
    return mul
}