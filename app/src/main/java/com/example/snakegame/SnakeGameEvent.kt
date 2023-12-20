package com.example.snakegame

import androidx.compose.ui.geometry.Offset

sealed class SnakeGameEvent {

    data object StartGame : SnakeGameEvent()
    data object PauseGame : SnakeGameEvent()
    data object RestartGame : SnakeGameEvent()
    data class Direction(val offset : Offset , val canvasWidth : Int) : SnakeGameEvent()

}