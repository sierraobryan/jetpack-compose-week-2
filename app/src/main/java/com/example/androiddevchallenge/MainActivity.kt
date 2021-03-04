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

import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androiddevchallenge.ui.theme.MyTheme

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                MyApp(viewModel)
            }
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun MyApp(mainViewModel: MainViewModel, modifier: Modifier = Modifier) {

    val width = (LocalContext.current.getSystemService(Context.WINDOW_SERVICE) as WindowManager)
        .defaultDisplay.width

    val seconds = mainViewModel.seconds.observeAsState()
    val minutes = mainViewModel.minutes.observeAsState()
    val hours = mainViewModel.hours.observeAsState()
    val running = mainViewModel.running.observeAsState()
    val finished = mainViewModel.finished.observeAsState()

    Surface(color = MaterialTheme.colors.background) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(40.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TimeColumn(value = hours.value, enabled = running.value != true) { mainViewModel.modifyTime(TimeUnit.HOUR, it) }
            Text(text = " : ", fontSize = 32.sp)
            TimeColumn(value = minutes.value, enabled = running.value != true) { mainViewModel.modifyTime(TimeUnit.MIN, it) }
            Text(text = " : ", fontSize = 32.sp)
            TimeColumn(value = seconds.value, enabled = running.value != true) { mainViewModel.modifyTime(TimeUnit.SEC, it) }
        }
        Row(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(start = 40.dp, end = 40.dp, top = 200.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedVisibility(visible = running.value != true) {
                Button(
                    onClick = { mainViewModel.startCountDown() },
                    enabled = !((seconds.value ?: 0) == 0 && (minutes.value ?: 0) == 0 && (hours.value ?: 0) == 0)
                ) {
                    Text(text = "Start")
                }
            }
        }
        Row(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(start = 40.dp, end = 40.dp, top = 200.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedVisibility(
                visible = running.value == true,
            ) {
                Button(onClick = { mainViewModel.cancel() }) {
                    Text(text = "Cancel")
                }
            }
        }
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(start = 40.dp, end = 40.dp, top = 120.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "3... 2... 1... Blast off!", fontSize = 24.sp)
        }
        Row(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(start = 40.dp, bottom = 40.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            AnimatedVisibility(
                visible = finished.value != true,
                exit = slideOut(
                    targetOffset = { intSize ->
                        IntOffset(intSize.height + width, - intSize.width - width)
                    },
                    animationSpec = tween(durationMillis = 800, easing = FastOutLinearInEasing)
                )
            ) {
                Text(text = String(Character.toChars(0x1F680)), fontSize = 24.sp)
            }
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun TimeColumn(value: Int?, enabled: Boolean, function: (Operation) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IncrementButton(Operation.ADD, enabled = enabled, function = function)
        Text(text = String.format("%02d", value ?: 0), fontSize = 32.sp)
        IncrementButton(Operation.SUBTRACT, enabled = enabled, function = function)
    }
}

@ExperimentalAnimationApi
@Composable
fun IncrementButton(operation: Operation, enabled: Boolean, function: (Operation) -> Unit) {
    AnimatedVisibility(
        visible = enabled
    ) {
        Button(
            onClick = { function.invoke(operation) },
            enabled = enabled,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.background,
                disabledBackgroundColor = MaterialTheme.colors.background
            ),
            elevation = ButtonDefaults.elevation(defaultElevation = 0.dp)
        ) {
            Text(
                text = if (operation == Operation.ADD) "++" else "--",
                fontWeight = FontWeight.Bold, fontSize = 24.sp
            )
        }
    }
}
