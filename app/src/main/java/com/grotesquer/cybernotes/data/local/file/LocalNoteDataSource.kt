package com.grotesquer.cybernotes.data.local.file

import android.content.Context
import com.grotesquer.cybernotes.data.NoteDataSource
import com.grotesquer.cybernotes.model.Note
import com.grotesquer.cybernotes.model.Note.Companion.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.slf4j.LoggerFactory
import java.io.File

class LocalNoteDataSource(context: Context) : NoteDataSource {
    private val _notes = mutableListOf<Note>()
    private val _notesFlow = MutableSharedFlow<List<Note>>(replay = 1)
    override val notesFlow: SharedFlow<List<Note>> = _notesFlow.asSharedFlow()

    private val logger = LoggerFactory.getLogger(LocalNoteDataSource::class.java)
    private val dataFile: File by lazy {
        File(context.filesDir, "cybernotes_data.json")
    }

    init {
        CoroutineScope(Dispatchers.IO).launch {
            loadFromFile()
        }
    }

    override suspend fun addNote(note: Note) {
        logger.info("Adding note with uid: ${note.uid}, title: ${note.title}")
        _notes.add(note)
        saveToFile()
        emitNotes()
    }

    override suspend fun removeNote(uid: String): Boolean {
        logger.info("Attempting to remove note with uid: $uid")
        val noteToRemove = _notes.find { it.uid == uid }
        return noteToRemove?.let {
            _notes.remove(it)
            logger.info("Note with uid: $uid removed successfully")
            saveToFile()
            emitNotes()
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

    private suspend fun loadFromFile() {
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
            emitNotes()
        } catch (e: Exception) {
            logger.error("Error loading notebook from file", e)
        }
    }

    override suspend fun getNoteByUid(noteUid: String): Note? {
        return _notes.find { it.uid == noteUid }
    }

    override suspend fun updateNote(updatedNote: Note) {
        val index = _notes.indexOfFirst { it.uid == updatedNote.uid }

        if (index >= 0) {
            _notes[index] = updatedNote
            saveToFile()
            emitNotes()
        } else {
            addNote(updatedNote)
        }
    }

    private suspend fun emitNotes() {
        _notesFlow.emit(_notes.toList())
    }
}
