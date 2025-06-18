package com.grotesquer.cybernotes.ui

sealed class Screen(val route: String) {
    object NotesList : Screen("notes_list")
    object NoteEdit : Screen("edit_note/{noteId}") {
        fun createRoute(noteId: String) = "edit_note/$noteId"
    }
}
