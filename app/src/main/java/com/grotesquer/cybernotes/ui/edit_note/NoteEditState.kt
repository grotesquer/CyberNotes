package com.grotesquer.cybernotes.ui.edit_note

import com.grotesquer.cybernotes.model.Note

data class NoteEditState(
    val note: Note = Note.create("", ""),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showDatePicker: Boolean = false,
    val showColorPicker: Boolean = false
)
