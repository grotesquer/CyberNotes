package com.grotesquer.cybernotes.ui.list_notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grotesquer.cybernotes.data.NoteRepository
import com.grotesquer.cybernotes.model.Note
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NotesListViewModel(
    private val repository: NoteRepository
) : ViewModel() {
    private val _state = MutableStateFlow(NotesListState())
    val state: StateFlow<NotesListState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<NotesListEffect>()
    val effects: SharedFlow<NotesListEffect> = _effects.asSharedFlow()

    init {
        viewModelScope.launch {
            repository.notesFlow.collect { notes ->
                _state.update { it.copy(notes = notes) }
            }
        }
    }

    fun handleEvent(event: NotesListEvent) {
        when (event) {
            is NotesListEvent.DeleteNote -> deleteNote(event.note)
            is NotesListEvent.SelectNote -> selectNote(event.note)
            is NotesListEvent.AddNote -> addNote()
        }
    }

    private fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.removeNote(note.uid)
        }
    }

    private fun selectNote(note: Note) {
        viewModelScope.launch {
            _effects.emit(NotesListEffect.NavigateToNoteDetail(note))
        }
    }

    private fun addNote() {
        viewModelScope.launch {
            _effects.emit(NotesListEffect.NavigateToAddNote)
        }
    }
}