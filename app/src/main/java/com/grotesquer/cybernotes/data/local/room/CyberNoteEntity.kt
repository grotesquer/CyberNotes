package com.grotesquer.cybernotes.data.local.room

import com.grotesquer.cybernotes.model.Importance
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.grotesquer.cybernotes.model.Note
import java.time.Instant
import java.time.ZoneId
import java.util.Date

@Entity(tableName = "notes")
data class CyberNoteEntity(
    @PrimaryKey val uid: String,
    val title: String,
    val content: String,
    val color: Int,
    val importance: String,
    val createdAt: Long,
    val selfDestructDate: Long? = null
) {
    fun toNote(): Note = Note.create(
        uid = uid,
        title = title,
        content = content,
        color = color,
        importance = when (importance.uppercase()) {
            "HIGH" -> Importance.HIGH
            "LOW" -> Importance.LOW
            else -> Importance.NORMAL
        },
        createdAt = Date(createdAt),
        selfDestructDate = selfDestructDate?.let {
            Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
        }
    )

    companion object {
        fun Note.toEntity(): CyberNoteEntity = CyberNoteEntity(
            uid = this.uid,
            title = this.title,
            content = this.content,
            color = this.color,
            importance = when (this.importance) {
                Importance.HIGH -> "HIGH"
                Importance.LOW -> "LOW"
                else -> "NORMAL"
            },
            createdAt = this.createdAt.time,
            selfDestructDate = this.selfDestructDate?.toEpochDay()
        )
    }
}