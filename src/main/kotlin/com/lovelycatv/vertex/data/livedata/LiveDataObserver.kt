package com.lovelycatv.vertex.data.livedata

/**
 * @author lovelycat
 * @since 2024-11-06 21:36
 * @version 1.0
 */
interface LiveDataObserver<T> {
    fun onChanged(newValue: T)
}

fun <T> LiveDataObserver(fx: (T) -> Unit): LiveDataObserver<T> {
    return object : LiveDataObserver<T> {
        override fun onChanged(newValue: T) {
            fx(newValue)
        }
    }
}