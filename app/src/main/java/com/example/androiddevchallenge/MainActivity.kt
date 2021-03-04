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

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androiddevchallenge.ui.theme.MyTheme

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                MyApp(viewModel)
            }
        }
    }
}

// Start building your app here!
@Composable
fun MyApp(mainViewModel: MainViewModel, modifier: Modifier = Modifier) {

    val seconds = mainViewModel.seconds.observeAsState()
    val minutes = mainViewModel.minutes.observeAsState()
    val hours = mainViewModel.hours.observeAsState()
    val running = mainViewModel.running.observeAsState()

    Surface(color = MaterialTheme.colors.background) {
        Column() {
            Text(text = "Ready... Set... GO!")
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(40.dp)
                    .align(Alignment.CenterHorizontally),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TimeColumn(value = hours.value, enabled = running.value == false) { mainViewModel.modifyTime(TimeUnit.HOUR, it) }
                Text(text = " : ", fontSize = 32.sp)
                TimeColumn(value = minutes.value, enabled = running.value == false) { mainViewModel.modifyTime(TimeUnit.MIN, it) }
                Text(text = " : ", fontSize = 32.sp)
                TimeColumn(value = seconds.value, enabled = running.value == false) { mainViewModel.modifyTime(TimeUnit.SEC, it) }
            }
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(start = 40.dp, end = 40.dp,)
                    .align(Alignment.CenterHorizontally),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = { mainViewModel.startCountDown() }) {
                    Text(text = "Start")
                }
                Button(onClick = { mainViewModel.cancel() }) {
                    Text(text = "Cancel")
                }
            }
        }
    }
}

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

@Composable
fun IncrementButton(operation: Operation, enabled: Boolean, function: (Operation) -> Unit) {
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
