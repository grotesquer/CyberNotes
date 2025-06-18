package com.grotesquer.cybernotes.data.remote

import android.util.Log
import com.grotesquer.cybernotes.data.NoteDataSource
import com.grotesquer.cybernotes.model.Note
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class RemoteNoteDataSource : NoteDataSource {
    private val _notes = mutableListOf<Note>()
    private val _notesFlow = MutableSharedFlow<List<Note>>(replay = 1)
    override val notesFlow: SharedFlow<List<Note>> = _notesFlow.asSharedFlow()

    override suspend fun addNote(note: Note) {
        Log.w("RemoteNoteDataSource", "Adding note remotely: ${note.title}")
        _notes.add(note)
        emitNotes()
    }

    override suspend fun removeNote(uid: String): Boolean {
        Log.w("RemoteNoteDataSource", "Removing note remotely: $uid")
        val noteToRemove = _notes.find { it.uid == uid }
        return noteToRemove?.let {
            _notes.remove(it)
            emitNotes()
            true
        } ?: false
    }

    override suspend fun getNoteByUid(noteUid: String): Note? {
        Log.w("RemoteNoteDataSource", "Getting note by uid: $noteUid")
        return _notes.find { it.uid == noteUid }
    }

    override suspend fun updateNote(updatedNote: Note) {
        Log.w("RemoteNoteDataSource", "Updating note remotely: ${updatedNote.title}")
        val index = _notes.indexOfFirst { it.uid == updatedNote.uid }
        if (index >= 0) {
            _notes[index] = updatedNote
        } else {
            _notes.add(updatedNote)
        }
        emitNotes()
    }

    private suspend fun emitNotes() {
        _notesFlow.emit(_notes.toList())
    }
}