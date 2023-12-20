package com.example.snakegame

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SnakeGameViewModel : ViewModel() {

    //for Both read and write
    private val _state = MutableStateFlow(SnakeGameState())
    //for only read
    val state = _state.asStateFlow()


    fun onEvent(event : SnakeGameEvent){
        when(event){
            SnakeGameEvent.StartGame -> {
                _state.update {snakeGameState ->
                    snakeGameState.copy(gameState = GameState.STARTED)
                }
                viewModelScope.launch {
                    while (state.value.gameState == GameState.STARTED){
                        val delayMills = when(state.value.snake.size){
                            in 1..5 ->120L
                            in 6..10 -> 110L
                            else -> 100L
                        }
                        delay(delayMills)
                        _state.value = updateGame(state.value)
                    }
                }
            }
            is SnakeGameEvent.Direction -> {
                updateDirection(event.offset,event.canvasWidth)
            }
            SnakeGameEvent.PauseGame ->{
                _state.update {snakeGameState->
                    snakeGameState.copy(
                       gameState = GameState.PAUSED
                    )

                }
            }
            SnakeGameEvent.RestartGame -> {
                _state.value = SnakeGameState()
            }
        }
    }

    private fun updateDirection(offset: Offset, canvasWidth: Int) {
        if (!state.value.isGameOver){
            val cellSize = canvasWidth / state.value.xAxisGridSize
            val tapX = (offset.x / cellSize).toInt()
            val tapY = (offset.y / cellSize).toInt()
            val head = state.value.snake.first()

            _state.update {snakeGameState ->
                snakeGameState.copy(
                    direction = when(state.value.direction){
                        Direction.UP,Direction.DOWN -> {
                            if (tapX < head.x) Direction.LEFT else Direction.RIGHT
                        }
                        Direction.RIGHT,Direction.LEFT ->{
                            if (tapY < head.y) Direction.UP else Direction.DOWN
                        }
                    }
                )

            }
        }
    }


    private fun updateGame(currentGame : SnakeGameState) : SnakeGameState{
        if (currentGame.isGameOver){
            return currentGame
        }

        val head = currentGame.snake.first()
        val xAxisGridSize = currentGame.xAxisGridSize
        val yAxisGridSize = currentGame.yAxisGridSize


        //Update Game
        val newHead = when(currentGame.direction){
            Direction.UP -> Coordinate(x = head.x , y = (head.y - 1))
            Direction.DOWN -> Coordinate(x = head.x , y = (head.y + 1))
            Direction.LEFT -> Coordinate(x = head.x - 1 , y = (head.y))
            Direction.RIGHT -> Coordinate(x = head.x +1 , y = (head.y))
        }


        //checking the snake is collided with itself or boundary
        if (
            currentGame.snake.contains(newHead) ||
            !isWithinBound(newHead,xAxisGridSize, yAxisGridSize)
            ){
            return currentGame.copy(isGameOver = true)
        }

        //Check snake eats the food
        var newSnake = mutableListOf(newHead) + currentGame.snake
        val newFood = if (newHead == currentGame.food) SnakeGameState.getRandomFoodCoordinate()
        else currentGame.food

        //updating snake length
        if (newHead != currentGame.food){
            newSnake = newSnake.toMutableList()
            newSnake.removeAt(newSnake.size -1)
        }


        return currentGame.copy(snake = newSnake , food = newFood)
    }

    private fun isWithinBound(
        coordinate : Coordinate,
        xAxisGridSize : Int,
        yAxisGridSize : Int,
    ) : Boolean{
        return coordinate.x in 1 until xAxisGridSize-1
                 && coordinate.y in 1 until yAxisGridSize-1
    }

}


