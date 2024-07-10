package com.example.minigames.server.model

data class SaveGameDto(val userId: String, val name: String, val gameState: String)

data class SharedGameDto(
    val shareduserId: String,
    val userId: String,
    val name: String,
    val gameState: String
)