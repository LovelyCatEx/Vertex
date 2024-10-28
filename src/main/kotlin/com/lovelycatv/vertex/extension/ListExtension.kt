package com.lovelycatv.vertex.extension

/**
 * @author lovelycat
 * @since 2024-10-28 22:05
 * @version 1.0
 */
class ListExtension private constructor()

fun <T> Iterable<T>.mergeToList(another: Iterable<T>): List<T> {
    return this.toMutableList() + another.toList()
}

fun <T> Iterable<T>.mergeToMutableList(another: Iterable<T>): MutableList<T> {
    return this.toMutableList().apply { addAll(another.toMutableList()) }
}

fun <A, B> Iterable<A>.leftJoin(another: Iterable<B>, fxOn: (A, B) -> Boolean): List<Pair<A, B?>> {
    val resultMap = mutableListOf<Pair<A, B?>>()
    for (a in this) {
        val t = mutableListOf<B>()
        for (b in another) {
            if (fxOn(a, b)) {
                t.add(b)
            }
        }
        if (t.isEmpty()) {
            resultMap.add(a to null)
        } else {
            for (target in t) {
                resultMap.add(a to target)
            }
        }
    }
    return resultMap
}

fun <A, B> Iterable<A>.rightJoin(another: Iterable<B>, fxOn: (B, A) -> Boolean): List<Pair<B, A?>> {
    return another.leftJoin(this, fxOn)
}