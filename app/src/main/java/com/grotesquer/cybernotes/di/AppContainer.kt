package com.grotesquer.cybernotes.di

import com.grotesquer.cybernotes.data.NoteRepository

interface AppContainer {
    val repository: NoteRepository
}

