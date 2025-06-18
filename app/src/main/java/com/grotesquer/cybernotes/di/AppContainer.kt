package com.grotesquer.cybernotes.di

import com.grotesquer.cybernotes.data.FileNotebook

interface AppContainer {
    val fileNotebook: FileNotebook
}

