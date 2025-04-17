package com.example.wavesoffood

data class NotificationData(
    val title: String,
    val body: String
)

data class NotificationRequest(
    val message: MessageData
)

data class MessageData(
    val token: String,
    val notification: NotificationData
)

