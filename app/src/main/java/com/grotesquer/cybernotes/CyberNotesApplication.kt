package com.grotesquer.cybernotes

import android.app.Application
import com.grotesquer.cybernotes.di.container.AppContainer
import com.grotesquer.cybernotes.di.container.NotesAppContainer

class CyberNotesApplication: Application(){
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = NotesAppContainer(context = this)
    }
}