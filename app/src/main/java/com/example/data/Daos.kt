package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage)

    @Update
    suspend fun updateMessage(message: ChatMessage)

    @Query("DELETE FROM chat_messages")
    suspend fun clearAllMessages()
}

@Dao
interface UserNoteDao {
    @Query("SELECT * FROM user_notes ORDER BY timestamp DESC")
    fun getAllNotes(): Flow<List<UserNote>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: UserNote)

    @Query("DELETE FROM user_notes WHERE id = :id")
    suspend fun deleteNoteById(id: Int)

    @Query("DELETE FROM user_notes")
    suspend fun clearAllNotes()
}

@Dao
interface CustomCommandDao {
    @Query("SELECT * FROM custom_commands ORDER BY pattern ASC")
    fun getAllCustomCommands(): Flow<List<CustomCommand>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomCommand(command: CustomCommand)

    @Query("DELETE FROM custom_commands WHERE pattern = :pattern")
    suspend fun deleteCustomCommand(pattern: String)
}
