package com.grotesquer.cybernotes.data

import com.grotesquer.cybernotes.model.Note
import com.grotesquer.cybernotes.model.json
import com.grotesquer.cybernotes.model.parse
import org.json.JSONObject
import java.io.File

class FileNotebook {
    private val _notes = mutableListOf<Note>()
    val notes: List<Note> get() = _notes.toList()

    fun addNote(note: Note) {
        _notes.add(note)
    }

    fun removeNote(uid: String): Boolean {
        val noteToRemove = _notes.find { it.uid == uid }
        return noteToRemove?.let { _notes.remove(it) } ?: false
    }

    fun saveToFile(file: File) {
        val notesArray = _notes.map { it.json.toString() }
        file.writeText(notesArray.joinToString("\n"))
    }

    fun loadFromFile(file: File) {
        if (!file.exists()) return

        val loadedNotes = mutableListOf<Note>()
        file.readLines().forEach { line ->
            try {
                val json = JSONObject(line)
                Note.parse(json)?.let { loadedNotes.add(it) }
            } catch (e: Exception) { }
        }

        _notes.clear()
        _notes.addAll(loadedNotes)
    }
}