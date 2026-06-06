package com.example.data

import kotlinx.coroutines.flow.Flow

class ChiomaRepository(private val db: AppDatabase) {
    val allMessages: Flow<List<ChatMessage>> = db.chatMessageDao().getAllMessages()
    val allNotes: Flow<List<UserNote>> = db.userNoteDao().getAllNotes()
    val allCustomCommands: Flow<List<CustomCommand>> = db.customCommandDao().getAllCustomCommands()

    suspend fun insertMessage(message: ChatMessage) {
        db.chatMessageDao().insertMessage(message)
    }

    suspend fun updateMessage(message: ChatMessage) {
        db.chatMessageDao().updateMessage(message)
    }

    suspend fun clearAllMessages() {
        db.chatMessageDao().clearAllMessages()
    }

    suspend fun insertNote(note: UserNote) {
        db.userNoteDao().insertNote(note)
    }

    suspend fun deleteNoteById(id: Int) {
        db.userNoteDao().deleteNoteById(id)
    }

    suspend fun clearAllNotes() {
        db.userNoteDao().clearAllNotes()
    }

    suspend fun insertCustomCommand(command: CustomCommand) {
        db.customCommandDao().insertCustomCommand(command)
    }

    suspend fun deleteCustomCommand(pattern: String) {
        db.customCommandDao().deleteCustomCommand(pattern)
    }
}
