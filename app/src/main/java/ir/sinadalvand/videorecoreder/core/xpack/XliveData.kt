/*
 *
 *   Created by Sina Dalvand on 8/7/2019
 *   Copyright (c) 2019 . All rights reserved.
 *
 *
 */

package ir.sinadalvand.videorecoreder.core.xpack

import android.util.Log
import androidx.annotation.MainThread
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean


class XliveData<X>(private val once: Boolean = false) : MutableLiveData<X>() {

    private val atomicBoolean = AtomicBoolean(false)

    @MainThread
    override fun observe(@NonNull owner: LifecycleOwner, @NonNull observer: Observer<in X>) {
        if (hasActiveObservers()) {
            Log.w(
                "XliveData",
                "Multiple observers registered but only one will be notified of changes."
            )
        }

        // Observe the internal MutableLiveData
        super.observe(owner, Observer<X> { t ->
            if (once) {
                if (atomicBoolean.compareAndSet(true, false)) {
                    observer.onChanged(t)
                }
            } else {
                observer.onChanged(t)
            }
        })
    }

    @MainThread
    override
    fun setValue(@Nullable t: X) {
        atomicBoolean.set(true)
        super.setValue(t)
    }

    override
    fun postValue(value: X) {
        atomicBoolean.set(true)
        super.postValue(value)
    }

}
