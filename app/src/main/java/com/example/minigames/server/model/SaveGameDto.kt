package com.example.minigames.server.model

data class SaveGameDto(val userId: String, val name: String, val gameState: String)

data class SharedGameDto(
    val toUserId: String,
    val fromUserId: String,
    val name: String,
    val gameState: String
)