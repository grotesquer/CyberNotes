package com.grotesquer.cybernotes.di.container

import android.content.Context
import com.grotesquer.cybernotes.data.NoteRepository
import com.grotesquer.cybernotes.data.local.LocalNoteDataSource
import com.grotesquer.cybernotes.data.remote.RemoteNoteDataSource
import com.grotesquer.cybernotes.di.network.WebServiceProvider

class NotesAppContainer(context: Context) : AppContainer {

    override val repository: NoteRepository = NoteRepository(
        remoteDataSource = RemoteNoteDataSource(
            apiService = WebServiceProvider.webService
        ),
        localDataSource = LocalNoteDataSource(context = context)
    )

}