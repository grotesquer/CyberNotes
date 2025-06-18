package com.grotesquer.cybernotes.data

import com.grotesquer.cybernotes.model.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

class NoteRepository(
    private val localDataSource: NoteDataSource,
    private val remoteDataSource: NoteDataSource
) : NoteDataSource {
    override val notesFlow: Flow<List<Note>> = flow {
        emitAll(localDataSource.notesFlow)
    }

    override suspend fun addNote(note: Note) {
        localDataSource.addNote(note)
        remoteDataSource.addNote(note)
    }

    override suspend fun removeNote(uid: String): Boolean {
        val localRemoved = localDataSource.removeNote(uid)
        val remoteRemoved = remoteDataSource.removeNote(uid)
        return localRemoved && remoteRemoved
    }

    override suspend fun getNoteByUid(noteUid: String): Note? {
        return localDataSource.getNoteByUid(noteUid) ?: remoteDataSource.getNoteByUid(noteUid)
    }

    override suspend fun updateNote(updatedNote: Note) {
        localDataSource.updateNote(updatedNote)
        remoteDataSource.updateNote(updatedNote)
    }
}