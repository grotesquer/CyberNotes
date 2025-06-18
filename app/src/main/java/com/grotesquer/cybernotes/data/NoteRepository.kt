package com.grotesquer.cybernotes.data

import com.grotesquer.cybernotes.data.remote.RemoteNoteDataSource
import com.grotesquer.cybernotes.model.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class NoteRepository(
    private val localDataSource: NoteDataSource,
    private val remoteDataSource: RemoteNoteDataSource
) : NoteDataSource {
    override val notesFlow: Flow<List<Note>> =
        combine(localDataSource.notesFlow, remoteDataSource.notesFlow) { local, remote ->
            remote + local.filter { localNote ->
                remote.none { it.uid == localNote.uid }
            }
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

    suspend fun sync() {
        remoteDataSource.syncNotes()
    }
}