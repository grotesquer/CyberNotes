package com.grotesquer.cybernotes.data.remote

import com.grotesquer.cybernotes.model.DtoRequest
import com.grotesquer.cybernotes.model.ElementResponse
import com.grotesquer.cybernotes.model.GetNotesResponse
import com.grotesquer.cybernotes.model.PatchNotesRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface NotesApiService {

    @POST("list")
    suspend fun addNote(
        @Header("X-Last-Known-Revision") revision: Int,
        @Body request: DtoRequest,
        @Header("X-Generate-Fails") generateFailsThreshold: Int? = null,
    ): ElementResponse

    @PUT("list/{id}")
    suspend fun updateNote(
        @Header("X-Last-Known-Revision") revision: Int,
        @Path("id") noteUid: String,
        @Body request: DtoRequest,
        @Header("X-Generate-Fails") generateFailsThreshold: Int? = null,
    ): ElementResponse

    @GET("list/{id}")
    suspend fun getNote(
        @Path("id") noteUid: String,
        @Header("X-Generate-Fails") generateFailsThreshold: Int? = null,
    ): ElementResponse

    @GET("list")
    suspend fun getNotes(
        @Header("X-Generate-Fails") generateFailsThreshold: Int? = null,
    ): GetNotesResponse


    @DELETE("list/{id}")
    suspend fun deleteNote(
        @Header("X-Last-Known-Revision") revision: Int,
        @Path("id") noteUid: String,
        @Header("X-Generate-Fails") generateFailsThreshold: Int? = null,
    ): ElementResponse

    @PATCH("list")
    suspend fun patchNotes(
        @Header("X-Last-Known-Revision") revision: Int,
        @Body request: PatchNotesRequest,
        @Header("X-Generate-Fails") generateFailsThreshold: Int? = null,
    ): GetNotesResponse

}