package com.grotesquer.cybernotes.data.remote

import com.grotesquer.cybernotes.data.NoteDataSource
import com.grotesquer.cybernotes.model.DtoRequest
import com.grotesquer.cybernotes.model.Note
import com.grotesquer.cybernotes.model.toDto
import com.grotesquer.cybernotes.model.toModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import retrofit2.HttpException
import java.io.IOException

class RemoteNoteDataSource(
    private val apiService: NotesApiService,
) : NoteDataSource {
    private val logger = LoggerFactory.getLogger(RemoteNoteDataSource::class.java)

    private val _notes = mutableListOf<Note>()
    private val _notesFlow = MutableSharedFlow<List<Note>>(replay = 1)
    override val notesFlow: SharedFlow<List<Note>> = _notesFlow.asSharedFlow()

    private var lastKnownRevision: Int = 0
    private val retryDelay = 1000L
    private val maxRetries = 5

    init {
        logger.info("Initializing RemoteNoteDataSource")
        try {
            CoroutineScope(Dispatchers.IO).launch {
                syncNotes()
            }
        } catch (e: Exception) {
            logger.error("Initial sync failed", e)
        }
    }

    override suspend fun addNote(note: Note) {
        executeWithRetry {
            val response = apiService.addNote(
                revision = lastKnownRevision,
                request = DtoRequest(note.toDto()),
                generateFailsThreshold = null
            )
            lastKnownRevision = response.revision
            _notes.add(note)
            emitNotes()
            logger.warn("Note added successfully: ${note.title}")
        }
    }

    override suspend fun removeNote(uid: String): Boolean {
        return executeWithRetry {
            val noteToRemove = _notes.find { it.uid == uid } ?: return@executeWithRetry false

            val response = apiService.deleteNote(
                revision = lastKnownRevision,
                noteUid = uid,
                generateFailsThreshold = null
            )
            lastKnownRevision = response.revision
            _notes.remove(noteToRemove)
            emitNotes()
            logger.warn("Note removed successfully: $uid")
            true
        } ?: false
    }

    override suspend fun getNoteByUid(noteUid: String): Note? {
        return try {
            val response = apiService.getNote(noteUid)
            response.noteDto.toModel()
        } catch (e: Exception) {
            logger.error("Error getting note: $noteUid", e)
            null
        }
    }

    override suspend fun updateNote(updatedNote: Note) {
        executeWithRetry {
            val response = apiService.updateNote(
                revision = lastKnownRevision,
                noteUid = updatedNote.uid,
                request = DtoRequest(updatedNote.toDto()),
                generateFailsThreshold = null
            )
            lastKnownRevision = response.revision
            val index = _notes.indexOfFirst { it.uid == updatedNote.uid }
            if (index >= 0) {
                _notes[index] = updatedNote
            } else {
                _notes.add(updatedNote)
            }
            emitNotes()
            logger.warn("Note updated successfully: ${updatedNote.title}")
        }
    }

    suspend fun syncNotes() {
        try {
            val response = apiService.getNotes()
            lastKnownRevision = response.revision
            logger.debug("Syncing notes, received revision: $lastKnownRevision")

            val remoteNotes = response.list.map { it.toModel() }
            _notes.clear()
            _notes.addAll(remoteNotes)
            emitNotes()
            logger.info("Notes synced successfully. Total notes: ${_notes.size}")
        } catch (e: Exception) {
            logger.error("Error syncing notes. Last known revision: $lastKnownRevision", e)
        }
    }

    private suspend fun <T> executeWithRetry(
        block: suspend () -> T,
    ): T? {
        var retryCount = 0
        var lastError: Exception? = null

        while (retryCount < maxRetries) {
            try {
                logger.debug("Attempt ${retryCount + 1}/$maxRetries with revision $lastKnownRevision")
                return block().also {
                    logger.debug("Operation succeeded on attempt ${retryCount + 1}")
                }
            } catch (e: HttpException) {
                lastError = e
                val responseBody = e.response()?.errorBody()?.string()
                logger.warn("Server error (${e.code()}), response: ${responseBody ?: "no body"}, retry $retryCount/$maxRetries")
            } catch (e: IOException) {
                lastError = e
                logger.warn("Network error: ${e.message}, retry $retryCount/$maxRetries")
            }

            retryCount++
            delay(retryDelay)
        }

        lastError?.let {
            logger.error(
                "Operation failed after $maxRetries retries. Last revision: $lastKnownRevision",
                it
            )
        }
        return null
    }

    private suspend fun emitNotes() {
        _notesFlow.emit(_notes.toList())
    }
}