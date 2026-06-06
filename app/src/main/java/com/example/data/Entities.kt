package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sender: String, // "user" or "chioma"
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val commandParsed: String? = null,
    val commandOutput: String? = null,
    val isSuccess: Boolean = true,
    val isVerboseExpanded: Boolean = false
)

@Entity(tableName = "user_notes")
data class UserNote(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "custom_commands")
data class CustomCommand(
    @PrimaryKey val pattern: String,
    val command: String
)
