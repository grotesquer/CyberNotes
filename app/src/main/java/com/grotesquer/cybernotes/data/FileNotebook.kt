package com.grotesquer.cybernotes.data

import com.grotesquer.cybernotes.model.Note
import com.grotesquer.cybernotes.model.Note.Companion.json
import org.json.JSONObject
import org.slf4j.LoggerFactory
import java.io.File

class FileNotebook {
    private val _notes = mutableListOf<Note>()
    val notes: List<Note> get() = _notes.toList()

    private val logger = LoggerFactory.getLogger(FileNotebook::class.java)

    fun addNote(note: Note) {
        logger.info("Adding note with uid: ${note.uid}, title: ${note.title}")
        _notes.add(note)
    }

    fun removeNote(uid: String): Boolean {
        logger.info("Attempting to remove note with uid: $uid")
        val noteToRemove = _notes.find { it.uid == uid }
        return noteToRemove?.let {
            _notes.remove(it)
            logger.info("Note with uid: $uid removed successfully")
            true
        } ?: run {
            logger.warn("Note with uid: $uid not found")
            false
        }
    }

    fun saveToFile(file: File) {
        logger.info("Saving notebook to file: ${file.absolutePath}")
        try {
            val notesArray = _notes.map { it.json.toString() }
            file.writeText(notesArray.joinToString("\n"))
            logger.info("Notebook saved successfully, ${_notes.size} notes stored")
        } catch (e: Exception) {
            logger.error("Error saving notebook to file", e)
        }
    }

    fun loadFromFile(file: File) {
        logger.info("Loading notebook from file: ${file.absolutePath}")
        if (!file.exists()) {
            logger.warn("File not found, skipping load")
            return
        }

        val loadedNotes = mutableListOf<Note>()
        try {
            file.readLines().forEach { line ->
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
}