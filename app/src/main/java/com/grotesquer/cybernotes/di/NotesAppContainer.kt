package com.grotesquer.cybernotes.di

import android.content.Context
import com.grotesquer.cybernotes.data.NoteRepository
import com.grotesquer.cybernotes.data.local.LocalNoteDataSource
import com.grotesquer.cybernotes.data.remote.RemoteNoteDataSource

class NotesAppContainer(context: Context): AppContainer {

    override val repository: NoteRepository = NoteRepository(
        remoteDataSource = RemoteNoteDataSource(),
        localDataSource = LocalNoteDataSource(context = context)
    )

}