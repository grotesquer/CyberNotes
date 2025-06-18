package com.grotesquer.cybernotes.ui.edit_note

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grotesquer.cybernotes.data.NoteRepository
import com.grotesquer.cybernotes.model.Importance
import com.grotesquer.cybernotes.model.Note
import com.grotesquer.cybernotes.ui.itemIdArg
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class NoteEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: NoteRepository
) : ViewModel() {
    private val noteUid: String = checkNotNull(savedStateHandle[itemIdArg])

    private val _state = mutableStateOf(NoteEditState())
    val state: NoteEditState get() = _state.value

    private val _effects = MutableSharedFlow<NoteEditEffect>()
    val effects: SharedFlow<NoteEditEffect> = _effects

    init {
        loadNote()
    }

    fun handleEvent(event: NoteEditEvent) {
        when (event) {
            is NoteEditEvent.LoadNote -> loadNote()
            is NoteEditEvent.UpdateTitle -> updateTitle(event.title)
            is NoteEditEvent.UpdateContent -> updateContent(event.content)
            is NoteEditEvent.UpdateSelfDestruct -> updateSelfDestruct(event.enabled)
            is NoteEditEvent.UpdateSelfDestructDate -> updateSelfDestructDate(event.date)
            is NoteEditEvent.UpdateColor -> updateColor(event.color)
            is NoteEditEvent.UpdateImportance -> updateImportance(event.importance)
            is NoteEditEvent.ShowDatePicker -> showDatePicker()
            is NoteEditEvent.HideDatePicker -> hideDatePicker()
            is NoteEditEvent.ShowColorPicker -> showColorPicker()
            is NoteEditEvent.HideColorPicker -> hideColorPicker()
            is NoteEditEvent.SaveNote -> saveNote()
            is NoteEditEvent.Cancel -> cancel()
        }
    }

    private fun loadNote() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val note = if (noteUid == "new") {
                    Note.create(title = "", content = "")
                } else {
                    repository.getNoteByUid(noteUid) ?: Note.create(
                        title = "Not Found",
                        content = "Note with id $noteUid not found",
                        uid = noteUid
                    )
                }
                _state.value = _state.value.copy(note = note, isLoading = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = "Failed to load note: ${e.message}",
                    isLoading = false
                )
                _effects.emit(NoteEditEffect.ShowError("Failed to load note"))
            }
        }
    }

    private fun updateTitle(title: String) {
        _state.value = _state.value.copy(
            note = _state.value.note.copy(title = title)
        )
    }

    private fun updateContent(content: String) {
        _state.value = _state.value.copy(
            note = _state.value.note.copy(content = content)
        )
    }

    private fun updateSelfDestruct(enabled: Boolean) {
        _state.value = _state.value.copy(
            note = _state.value.note.copy(
                selfDestructDate = if (enabled) LocalDate.now().plusDays(7) else null
            )
        )
    }

    private fun updateSelfDestructDate(date: LocalDate?) {
        _state.value = _state.value.copy(
            note = _state.value.note.copy(selfDestructDate = date)
        )
    }

    private fun updateColor(color: Int) {
        _state.value = _state.value.copy(
            note = _state.value.note.copy(color = color)
        )
    }

    private fun updateImportance(importance: Importance) {
        _state.value = _state.value.copy(
            note = _state.value.note.copy(importance = importance)
        )
    }

    private fun showDatePicker() {
        _state.value = _state.value.copy(showDatePicker = true)
    }

    private fun hideDatePicker() {
        _state.value = _state.value.copy(showDatePicker = false)
    }

    private fun showColorPicker() {
        _state.value = _state.value.copy(showColorPicker = true)
    }

    private fun hideColorPicker() {
        _state.value = _state.value.copy(showColorPicker = false)
    }

    private fun saveNote() {
        viewModelScope.launch {
            try {
                if (noteUid == "new") {
                    repository.addNote(note = _state.value.note)
                } else {
                    repository.updateNote(updatedNote = _state.value.note)
                }
                _effects.emit(NoteEditEffect.NavigateBack)
            } catch (e: Exception) {
                _effects.emit(NoteEditEffect.ShowError("Failed to save note: ${e.message}"))
            }
        }
    }

    private fun cancel() {
        viewModelScope.launch {
            _effects.emit(NoteEditEffect.NavigateBack)
        }
    }
}