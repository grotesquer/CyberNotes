package com.grotesquer.cybernotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.grotesquer.cybernotes.ui.NotesApp
import com.grotesquer.cybernotes.ui.theme.CyberNotesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CyberNotesTheme {
                NotesApp()
            }
        }
    }
}
