package com.grotesquer.cybernotes.di.container

import com.grotesquer.cybernotes.data.NoteRepository

interface AppContainer {
    val repository: NoteRepository
}

