package com.grotesquer.cybernotes.ui.list_notes

import com.grotesquer.cybernotes.model.Note

data class NotesListState(
    val notes: List<Note> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)