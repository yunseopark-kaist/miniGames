package com.example.minigames.server.model



data class Relationship(
    val requesterId: Int,
    val recipientId: Int,
    val isAccepted: Boolean
)
data class CreateRequestBody(val requesterId: Int, val recipientId: Int)