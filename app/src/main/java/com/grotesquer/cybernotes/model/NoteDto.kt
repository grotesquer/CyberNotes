package com.grotesquer.cybernotes.model

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Date
import android.graphics.Color as AndroidColor

@Serializable
data class NoteDto(
    @SerialName("id") val id: String,
    @SerialName("text") val text: String,
    @SerialName("importance") val importance: String,
    @SerialName("deadline") val deadline: Long? = null,
    @SerialName("done") val done: Boolean,
    @SerialName("created_at") val createdAt: Long,
    @SerialName("changed_at") val changedAt: Long = Date().time,
    @SerialName("last_updated_by") val lastUpdatedBy: String,
    @SerialName("color") val color: String? = null,
)

fun Note.toDto(): NoteDto {
    val combinedText = if (content.isNotEmpty()) "$title\n$content" else title

    return NoteDto(
        id = this.uid,
        text = combinedText,
        importance = when (this.importance) {
            Importance.HIGH -> "important"
            Importance.LOW -> "low"
            else -> "basic"
        },
        done = false,
        lastUpdatedBy = "android_client",
        createdAt = this.createdAt.time,
        changedAt = Date().time,
        color = this.color.takeIf { it != AndroidColor.WHITE }?.let {
            String.format("#%06X", 0xFFFFFF and it)
        }
    )
}

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
fun NoteDto.toModel(): Note {
    val (title, content) = if (text.contains('\n')) {
        val splitIndex = text.indexOf('\n')
        text.substring(0, splitIndex) to text.substring(splitIndex + 1)
    } else {
        text to ""
    }

    return Note.create(
        uid = this.id,
        title = title,
        content = content,
        color = color?.let { AndroidColor.parseColor(it) } ?: AndroidColor.WHITE,
        importance = when (importance.lowercase()) {
            "important" -> Importance.HIGH
            "low" -> Importance.LOW
            else -> Importance.NORMAL
        },
        selfDestructDate = deadline?.let {
            LocalDate.ofInstant(
                Instant.ofEpochSecond(it),
                ZoneOffset.UTC
            )
        },
        createdAt = Date(createdAt)
    )
}
