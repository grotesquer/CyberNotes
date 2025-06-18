package com.grotesquer.cybernotes.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class NoteDto(
    @SerialName("id") val uid: String,

    val text: String,
    val importance: String,
    val deadline: Long? = null,
    @SerialName("done") val isDone: Boolean,
    @SerialName("created_at") val createdAt: Long,
    @SerialName("changed_at") val changedAt: Long = Date().time,
    @SerialName("last_updated_by") val lastUpdatedBy: String,
    val color: String? = null,
)