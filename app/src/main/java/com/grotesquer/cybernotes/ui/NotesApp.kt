package com.grotesquer.cybernotes.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.grotesquer.cybernotes.ui.edit_note.NoteEditScreen
import com.grotesquer.cybernotes.ui.list_notes.NotesListScreen

@Composable
fun NotesApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.NotesList.route
    ) {
        composable(Screen.NotesList.route) {
            NotesListScreen(
                onNoteClick = { note ->
                    navController.navigate(Screen.NoteEdit.createRoute(note.uid))
                },
                onAddNote = {
                    navController.navigate(Screen.NoteEdit.createRoute("new"))
                }
            )
        }
        composable(
            route = Screen.NoteEdit.route,
            arguments = listOf(navArgument(itemIdArg) {
                type = NavType.StringType
            })
        ) {
            NoteEditScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}