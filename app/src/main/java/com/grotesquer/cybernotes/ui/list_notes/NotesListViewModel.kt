package com.grotesquer.cybernotes.ui.list_notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grotesquer.cybernotes.model.Importance
import com.grotesquer.cybernotes.model.Note
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NotesListViewModel : ViewModel() {
    private val _state = MutableStateFlow(NotesListState())
    val state: StateFlow<NotesListState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<NotesListEffect>()
    val effects: SharedFlow<NotesListEffect> = _effects.asSharedFlow()

    init {
        viewModelScope.launch {
            _state.update { it.copy(notes = loadInitialNotes()) }
        }
    }

    fun handleEvent(event: NotesListEvent) {
        when (event) {
            is NotesListEvent.LoadNotes -> loadNotes()
            is NotesListEvent.DeleteNote -> deleteNote(event.note)
            is NotesListEvent.SelectNote -> selectNote(event.note)
            is NotesListEvent.AddNote -> addNote()
        }
    }

    private fun loadNotes() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                _state.update { it.copy(notes = loadInitialNotes(), isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    private fun deleteNote(note: Note) {
        viewModelScope.launch {
            _state.update { currentState ->
                currentState.copy(notes = currentState.notes - note)
            }
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

    private fun loadInitialNotes(): List<Note> {
        return listOf(
            Note.create(
                title = "System Alert",
                content = "Neural network anomaly detected in sector 7",
                importance = Importance.HIGH
            ),
            Note.create(
                title = "Data Stream",
                content = "Monitoring encrypted channels...",
                importance = Importance.NORMAL
            ),
            Note.create(
                title = "Code Fragment",
                content = "01001000 01101001 00100000 01010100 01101000 01100101 01110010 01100101",
                importance = Importance.LOW
            ),
            Note.create(
                title = "Security Breach",
                content = "Unauthorized access attempt from external node",
                importance = Importance.HIGH
            ),
            Note.create(
                title = "Maintenance Log",
                content = "System diagnostics running... All circuits nominal",
                importance = Importance.NORMAL
            )
        )
    }
}
