/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.CountDownTimer
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class Operation {
    ADD, SUBTRACT
}

enum class TimeUnit {
    SEC, MIN, HOUR
}

class MainViewModel : ViewModel() {

    private var pauseTimer: CountDownTimer? = null

    val seconds = MutableLiveData(10)
    val minutes = MutableLiveData(1)
    val hours = MutableLiveData(0)
    val running = MutableLiveData(false)
    val finished = MutableLiveData(false)

    fun startCountDown() {
        cancel()
        pauseTimer = object : CountDownTimer((getTimeInSecond() * 1000).toLong(), 1000) {

            override fun onTick(millisUntilFinished: Long) {
                val hourTime = (millisUntilFinished / 1000 / 60 / 60).toInt()
                val minuteTime = (millisUntilFinished / 1000 / 60 % 60).toInt()
                val secondTime = (millisUntilFinished / 1000 % 60).toInt()
                if (secondTime != seconds.value) {
                    seconds.postValue(secondTime)
                }
                if (minuteTime != minutes.value) {
                    minutes.postValue(minuteTime)
                }
                if (hourTime != hours.value) {
                    hours.postValue(hourTime)
                }
            }

            override fun onFinish() {
                viewModelScope.launch {
                    finished.postValue(true)
                    delay(2000)
                    running.postValue(false)
                    finished.postValue(false)
                }
            }
        }
        pauseTimer?.start()
        running.postValue(true)
    }

    fun cancel() {
        pauseTimer?.cancel()
        running.postValue(false)
    }

    fun modifyTime(timeUnit: TimeUnit, operation: Operation) {
        when (timeUnit) {
            TimeUnit.SEC -> seconds.postValue(
                operation(seconds.value ?: 0, operation).coerceIn(0, 59)
            )
            TimeUnit.MIN -> minutes.postValue(
                operation(minutes.value ?: 0, operation).coerceIn(0, 59)
            )
            TimeUnit.HOUR -> hours.postValue(
                operation(hours.value ?: 0, operation).coerceIn(0, 99)
            )
        }
    }

    private fun operation(currentValue: Int, operation: Operation): Int {
        return when (operation) {
            Operation.ADD -> currentValue + 1
            Operation.SUBTRACT -> currentValue - 1
        }
    }

    private fun getTimeInSecond() = ((hours.value ?: 0) * 60 * 60) + ((minutes.value ?: 0) * 60) + (seconds.value ?: 0)
}
