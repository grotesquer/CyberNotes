package com.grotesquer.cybernotes.data

import com.grotesquer.cybernotes.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteDataSource {
    val notesFlow: Flow<List<Note>>
    suspend fun addNote(note: Note)
    suspend fun removeNote(uid: String): Boolean
    suspend fun getNoteByUid(noteUid: String): Note?
    suspend fun updateNote(updatedNote: Note)
}