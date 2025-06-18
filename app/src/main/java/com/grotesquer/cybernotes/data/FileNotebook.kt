package com.grotesquer.cybernotes.data

import android.content.Context
import com.grotesquer.cybernotes.model.Note
import com.grotesquer.cybernotes.model.Note.Companion.json
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.json.JSONObject
import org.slf4j.LoggerFactory
import java.io.File

class FileNotebook(context: Context) {
    private val _notes = mutableListOf<Note>()
    private val _notesFlow = MutableSharedFlow<List<Note>>(replay = 1)
    val notesFlow: SharedFlow<List<Note>> = _notesFlow.asSharedFlow()

    private val logger = LoggerFactory.getLogger(FileNotebook::class.java)
    private val dataFile: File by lazy {
        File(context.filesDir, "cybernotes_data.json")
    }

    init {
        loadFromFile()
    }

    fun addNote(note: Note) {
        logger.info("Adding note with uid: ${note.uid}, title: ${note.title}")
        _notes.add(note)
        saveToFile()
    }

    fun removeNote(uid: String): Boolean {
        logger.info("Attempting to remove note with uid: $uid")
        val noteToRemove = _notes.find { it.uid == uid }
        return noteToRemove?.let {
            _notes.remove(it)
            logger.info("Note with uid: $uid removed successfully")
            saveToFile()
            true
        } ?: run {
            logger.warn("Note with uid: $uid not found")
            false
        }
    }

    private fun saveToFile() {
        logger.info("Saving notebook to file: ${dataFile.absolutePath}")
        try {
            val notesArray = _notes.map { it.json.toString() }
            dataFile.writeText(notesArray.joinToString("\n"))
            logger.info("Notebook saved successfully, ${_notes.size} notes stored")
        } catch (e: Exception) {
            logger.error("Error saving notebook to file", e)
        }
    }

    private fun loadFromFile() {
        logger.info("Loading notebook from file: ${dataFile.absolutePath}")
        if (!dataFile.exists()) {
            logger.warn("File not found, skipping load")
            return
        }

        val loadedNotes = mutableListOf<Note>()
        try {
            dataFile.readLines().forEach { line ->
                try {
                    val json = JSONObject(line)
                    Note.parse(json)?.let { loadedNotes.add(it) }
                } catch (e: Exception) {
                    logger.warn("Failed to parse note from JSON: $line", e)
                }
            }

            _notes.clear()
            _notes.addAll(loadedNotes)
            logger.info("Notebook loaded successfully, ${_notes.size} notes loaded")
        } catch (e: Exception) {
            logger.error("Error loading notebook from file", e)
        }
    }

    fun getNoteByUid(noteUid: String): Note? {
        return _notes.find { it.uid == noteUid }
    }

    fun updateNote(updatedNote: Note) {
        val index = _notes.indexOfFirst { it.uid == updatedNote.uid }

        if (index >= 0) {
            _notes[index] = updatedNote
            saveToFile()
        } else {
            addNote(updatedNote)
            saveToFile()
        }
    }
}