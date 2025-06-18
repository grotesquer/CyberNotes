package com.grotesquer.cybernotes.data.local.room

import com.grotesquer.cybernotes.data.NoteDataSource
import com.grotesquer.cybernotes.data.local.room.CyberNoteEntity.Companion.toEntity
import com.grotesquer.cybernotes.model.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomDataSource(
    private val noteDao: NoteDao
) : NoteDataSource {
    override val notesFlow: Flow<List<Note>>
        get() = noteDao.getAllNotesFlow()
            .map { entities ->
                entities.map { it.toNote() }
                    .sortedByDescending { it.createdAt }
            }

    override suspend fun addNote(note: Note) {
        noteDao.insertNote(note.toEntity())
    }

    override suspend fun removeNote(uid: String): Boolean {
        return noteDao.getNoteById(uid)?.let { entity ->
            noteDao.deleteNote(entity)
            true
        } ?: false
    }

    override suspend fun getNoteByUid(noteUid: String): Note? {
        return noteDao.getNoteById(noteUid)?.toNote()
    }

    override suspend fun updateNote(updatedNote: Note) {
        noteDao.insertNote(updatedNote.toEntity())
    }
}