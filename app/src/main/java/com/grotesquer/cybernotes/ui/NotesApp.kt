package com.grotesquer.cybernotes.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.grotesquer.cybernotes.model.Note
import com.grotesquer.cybernotes.ui.edit_note.NoteEditScreen
import com.grotesquer.cybernotes.ui.list_notes.NotesListScreen
import com.grotesquer.cybernotes.ui.list_notes.NotesListViewModel


@Composable
fun NotesApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "notes_list"
    ) {
        composable("notes_list") {
            NotesListScreen(
                onNoteClick = { note ->
                    navController.navigate("edit_note/${note.uid}")
                },
                onAddNote = {
                    navController.navigate("edit_note/new")
                }
            )
        }
        composable("edit_note/{noteId}") { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId") ?: "new"
            val note = if (noteId == "new") {
                Note.create(title = "", content = "")
            } else {
                Note.create(
                    title = "Existing Note",
                    content = "Note content",
                    uid = noteId
                )
            }

            NoteEditScreen(
                note = note,
                onSave = { savedNote ->
                    navController.popBackStack()
                },
                onCancel = {
                    navController.popBackStack()
                }
            )
        }
    }
}