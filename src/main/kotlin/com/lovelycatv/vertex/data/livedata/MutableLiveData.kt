package com.lovelycatv.vertex.data.livedata

/**
 * @author lovelycat
 * @since 2024-11-06 22:00
 * @version 1.0
 */
class MutableLiveData<T>(data: T) : LiveData<T>(data) {
    public override fun postValue(newValue: T) {
        super.postValue(newValue)
    }
}