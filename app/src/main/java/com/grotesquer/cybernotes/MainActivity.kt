package com.grotesquer.cybernotes

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import com.grotesquer.cybernotes.model.Importance
import com.grotesquer.cybernotes.model.Note
import com.grotesquer.cybernotes.ui.edit_note.NoteEditScreen
import com.grotesquer.cybernotes.ui.theme.CyberNotesTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CyberNotesTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NoteEditScreen(
                        note = Note.create(
                            title = "test title",
                            content = "content of note",
                            color = Color.Blue.toArgb(),
                            importance = Importance.HIGH
                        ),
                        onSave = {},
                        onCancel = {}
                    )
                }
            }
        }
    }
}
