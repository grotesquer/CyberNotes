package com.grotesquer.cybernotes.data.local.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes")
    fun getAllNotesFlow(): Flow<List<CyberNoteEntity>>

    @Query("SELECT * FROM notes WHERE uid = :id")
    suspend fun getNoteById(id: String): CyberNoteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: CyberNoteEntity)

    @Delete
    suspend fun deleteNote(note: CyberNoteEntity)

}