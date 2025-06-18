package com.grotesquer.cybernotes.di

import android.content.Context
import com.grotesquer.cybernotes.data.FileNotebook

class NotesAppContainer(context: Context): AppContainer {

    override val fileNotebook: FileNotebook = FileNotebook(context = context)

}