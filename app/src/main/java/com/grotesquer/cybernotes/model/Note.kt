package com.grotesquer.cybernotes.model

import android.graphics.Color
import org.json.JSONObject
import java.util.UUID

data class Note private constructor(
    val uid: String,
    val title: String,
    val content: String,
    val color: Int,
    val importance: Importance
) {
    companion object {
        fun create(
            title: String,
            content: String,
            color: Int = Color.WHITE,
            importance: Importance = Importance.NORMAL,
            uid: String = UUID.randomUUID().toString()
        ): Note {
            return Note(
                uid = uid,
                title = title,
                content = content,
                color = color,
                importance = importance
            )
        }
    }
}

fun Note.Companion.parse(json: JSONObject): Note? {
    return try {
        val uid = json.optString("uid", UUID.randomUUID().toString())
        val title = json.getString("title")
        val content = json.getString("content")
        val color = json.optInt("color", Color.WHITE)
        val importance = when (json.optString("importance")) {
            "LOW" -> Importance.LOW
            "HIGH" -> Importance.HIGH
            else -> Importance.NORMAL
        }

        Note.create(
            uid = uid,
            title = title,
            content = content,
            color = color,
            importance = importance
        )
    } catch (e: Exception) {
        null
    }
}

val Note.json: JSONObject
    get() {
        val json = JSONObject().apply {
            put("uid", uid)
            put("title", title)
            put("content", content)
        }

        if (color != Color.WHITE) {
            json.put("color", color)
        }

        if (importance != Importance.NORMAL) {
            json.put("importance", importance.name)
        }

        return json
    }
