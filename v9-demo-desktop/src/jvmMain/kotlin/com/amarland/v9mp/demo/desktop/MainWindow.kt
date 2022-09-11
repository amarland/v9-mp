@file:JvmName("MainWindow")

package com.amarland.v9mp.demo.desktop

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import com.amarland.v9mp.demo.common.Demo

fun main() = singleWindowApplication(
    state = WindowState(
        position = WindowPosition(Alignment.Center),
        width = 700.dp,
        height = 900.dp
    ),
    title = "v9mp Demo"
) { Demo() }
