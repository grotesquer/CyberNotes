package com.grotesquer.cybernotes.di.network

import com.grotesquer.cybernotes.data.remote.NotesApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

internal object WebServiceProvider {

    private const val SERVICE_ENDPOINT = "https://hive.mrdekk.ru/todo/"
    private const val API_ACCESS_KEY = "7bbc085d-090f-4550-ac8a-884853d6ddbe"
    private val JSON_MIME_TYPE = "application/json".toMediaTypeOrNull()!!

    private val jsonParser = Json {
        coerceInputValues = true
        ignoreUnknownKeys = true
        allowStructuredMapKeys = true
    }

    private fun createHttpClient(): OkHttpClient {
        val logger = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient().newBuilder().apply {
            addInterceptor(logger)
            addInterceptor { requestChain ->
                val modifiedRequest = requestChain.request().withAuthHeaders()
                requestChain.proceed(modifiedRequest)
            }
        }.build()
    }

    private fun Request.withAuthHeaders(): Request {
        return this.newBuilder().apply {
            header("Authorization", "Bearer $API_ACCESS_KEY")
            header("Accept", "application/json")
            header("Content-Type", "application/json")
            this@withAuthHeaders.headers["X-Generate-Fails"]?.let {
                header("X-Generate-Fails", it)
            }
        }.build()
    }

    private fun createRetrofitInstance(): Retrofit {
        return Retrofit.Builder().apply {
            baseUrl(SERVICE_ENDPOINT)
            client(createHttpClient())
            addConverterFactory(
                jsonParser.asConverterFactory(JSON_MIME_TYPE)
            )
        }.build()
    }

    val webService: NotesApiService by lazy {
        createRetrofitInstance().create(NotesApiService::class.java)
    }
}