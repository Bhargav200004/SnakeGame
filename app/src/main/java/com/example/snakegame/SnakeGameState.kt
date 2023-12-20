package com.example.snakegame

import kotlin.random.Random

data class SnakeGameState(
    val xAxisGridSize : Int = 20,
    val yAxisGridSize : Int = 30,
    val direction : Direction = Direction.RIGHT,
    val snake : List<Coordinate> = listOf(Coordinate(x = 5 , y = 5)),
    val food : Coordinate = getRandomFoodCoordinate(),
    val isGameOver : Boolean = false,
    val gameState: GameState = GameState.IDLE
){
    companion object{
        fun getRandomFoodCoordinate() : Coordinate{
            return Coordinate(
                x = Random.nextInt(from = 1 , until = 19),
                y = Random.nextInt(from = 1 , until = 29)
            )
        }
    }
}

enum class GameState{
    IDLE,
    STARTED,
    PAUSED
}


enum class Direction{
    RIGHT,
    LEFT,
    DOWN,
    UP
}

data class Coordinate(
    val x : Int ,
    val y : Int
)