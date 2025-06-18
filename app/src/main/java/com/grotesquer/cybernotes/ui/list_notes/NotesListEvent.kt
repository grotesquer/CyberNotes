package com.grotesquer.cybernotes.ui.list_notes

import com.grotesquer.cybernotes.model.Note

sealed class NotesListEvent {
    data class DeleteNote(val note: Note) : NotesListEvent()
    data class SelectNote(val note: Note) : NotesListEvent()
    object AddNote : NotesListEvent()
}

sealed class NotesListEffect {
    data class NavigateToNoteDetail(val note: Note) : NotesListEffect()
    object NavigateToAddNote : NotesListEffect()
}