package com.grotesquer.cybernotes.ui.edit_note

import com.grotesquer.cybernotes.model.Importance
import java.time.LocalDate

sealed class NoteEditEvent {
    data class LoadNote(val noteId: String) : NoteEditEvent()
    data class UpdateTitle(val title: String) : NoteEditEvent()
    data class UpdateContent(val content: String) : NoteEditEvent()
    data class UpdateSelfDestruct(val enabled: Boolean) : NoteEditEvent()
    data class UpdateSelfDestructDate(val date: LocalDate?) : NoteEditEvent()
    data class UpdateColor(val color: Int) : NoteEditEvent()
    data class UpdateImportance(val importance: Importance) : NoteEditEvent()
    object ShowDatePicker : NoteEditEvent()
    object HideDatePicker : NoteEditEvent()
    object ShowColorPicker : NoteEditEvent()
    object HideColorPicker : NoteEditEvent()
    object SaveNote : NoteEditEvent()
    object Cancel : NoteEditEvent()
}

sealed class NoteEditEffect {
    object NavigateBack : NoteEditEffect()
    data class ShowError(val message: String) : NoteEditEffect()
}