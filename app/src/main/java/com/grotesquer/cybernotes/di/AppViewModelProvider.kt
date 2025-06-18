package com.grotesquer.cybernotes.di

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.grotesquer.cybernotes.CyberNotesApplication
import com.grotesquer.cybernotes.ui.edit_note.NoteEditViewModel
import com.grotesquer.cybernotes.ui.list_notes.NotesListViewModel

object AppViewModelProvider {

    val Factory = viewModelFactory {
        initializer {
            NotesListViewModel(
                repository = notesApplication().container.repository,
            )
        }

        initializer {
            NoteEditViewModel(
                savedStateHandle = this.createSavedStateHandle(),
                repository = notesApplication().container.repository,
            )
        }
    }
}

fun CreationExtras.notesApplication(): CyberNotesApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as CyberNotesApplication)