package com.lovelycatv.vertex.data.livedata

import kotlin.concurrent.Volatile

/**
 * @author lovelycat
 * @since 2024-11-05 19:22
 * @version 1.0
 */
abstract class LiveData<T>(
    @Volatile
    private var data: T
) {
    val value: T get() = this.data

    private val observers = mutableListOf<LiveDataObserver<T>>()

    protected open fun postValue(newValue: T) {
        this.data = newValue
        notifyAllObservers(newValue)
    }

    fun observe(observer: (T) -> Unit): LiveDataObserver<T> {
        val t = LiveDataObserver(observer)
        this.observers.add(t)
        return t
    }

    fun observe(observer: LiveDataObserver<T>) {
        this.observers.add(observer)
    }

    fun removeObserver(observer: LiveDataObserver<T>) {
        this. observers.remove(observer)
    }

    private fun notifyAllObservers(newValue: T) {
        observers.forEach {
            it.onChanged(newValue)
        }
    }
}