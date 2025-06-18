package com.grotesquer.cybernotes.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PatchNotesRequest(
    val list: List<NoteDto>
)

@Serializable
data class DtoRequest(
    @SerialName("element") val noteDto: NoteDto
)

@Serializable
data class ElementResponse(
    val status: String,
    @SerialName("element") val noteDto: NoteDto,
    val revision: Int
)

@Serializable
data class GetNotesResponse(
    val status: String,
    val list: List<NoteDto>,
    val revision: Int
)
