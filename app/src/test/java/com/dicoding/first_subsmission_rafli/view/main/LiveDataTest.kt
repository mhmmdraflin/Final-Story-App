package com.dicoding.first_subsmission_rafli.view.main

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@VisibleForTesting(otherwise = VisibleForTesting.NONE)
fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS,
    afterObserve: () -> Unit = {}
): T {
    var data: T? = null
    val latch = CountDownLatch(1)
    val observe = object : Observer<T> {
        override fun onChanged(t: T) {
            data = t
            latch.countDown()
            this@getOrAwaitValue.removeObserver(this)
        }
    }
    this.observeForever(observe)
    try {
        afterObserve.invoke()
        if (!latch.await(time, timeUnit)) {
            throw AssertionError("LiveData value was never set.")
        }
    } finally {
        this.removeObserver(observe)
    }
    @Suppress("UNCHECKED_CAST")
    return data as T
}
