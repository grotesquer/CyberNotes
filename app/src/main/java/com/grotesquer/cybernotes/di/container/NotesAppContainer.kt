package com.grotesquer.cybernotes.di.container

import android.content.Context
import com.grotesquer.cybernotes.data.NoteRepository
import com.grotesquer.cybernotes.data.local.file.LocalNoteDataSource
import com.grotesquer.cybernotes.data.local.room.CyberNotesDatabase
import com.grotesquer.cybernotes.data.local.room.RoomDataSource
import com.grotesquer.cybernotes.data.remote.RemoteNoteDataSource
import com.grotesquer.cybernotes.di.network.WebServiceProvider

class NotesAppContainer(context: Context) : AppContainer {

    override val repository: NoteRepository = NoteRepository(
        remoteDataSource = RemoteNoteDataSource(
            apiService = WebServiceProvider.webService
        ),
        localDataSource = RoomDataSource(
            noteDao = CyberNotesDatabase.getInstance(context).noteDao()

        )
        // localDataSource = LocalNoteDataSource(context = context)
    )
}