package com.grotesquer.cybernotes.ui.edit_note

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.CalendarLocale
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.grotesquer.cybernotes.model.Importance
import com.grotesquer.cybernotes.model.Note
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditScreen(
    note: Note,
    onSave: (Note) -> Unit,
    onCancel: () -> Unit,
) {
    var editedNote by remember { mutableStateOf(note) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showColorPicker by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Редактирование заметки") },
                actions = {
                    TextButton(onClick = { onCancel() }) {
                        Text("Отмена")
                    }
                    TextButton(onClick = { onSave(editedNote) }) {
                        Text("Сохранить")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            NoteTitleField(
                title = editedNote.title,
                onTitleChange = { editedNote = editedNote.copy(title = it) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            NoteContentField(
                content = editedNote.content,
                onContentChange = { editedNote = editedNote.copy(content = it) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            SelfDestructSection(
                hasSelfDestruct = editedNote.selfDestructDate != null,
                selfDestructDate = editedNote.selfDestructDate,
                onSelfDestructChange = { enabled ->
                    editedNote = editedNote.copy(
                        selfDestructDate = if (enabled) LocalDate.now().plusDays(7) else null
                    )
                },
                onDateSelected = { date ->
                    editedNote = editedNote.copy(selfDestructDate = date)
                    showDatePicker = false
                },
                showDatePicker = showDatePicker,
                onShowDatePicker = { showDatePicker = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            ColorSelectionSection(
                selectedColor = Color(editedNote.color),
                onColorSelected = { color ->
                    editedNote = editedNote.copy(color = color.toArgb())
                },
                showColorPicker = showColorPicker,
                onShowColorPicker = { showColorPicker = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            ImportanceSelectionSection(
                importance = editedNote.importance,
                onImportanceSelected = { importance ->
                    editedNote = editedNote.copy(importance = importance)
                }
            )
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismiss = { showDatePicker = false },
            onDateSelected = { date ->
                editedNote = editedNote.copy(selfDestructDate = date)
                showDatePicker = false
            }
        )
    }

    if (showColorPicker) {
        ColorPickerDialog(
            initialColor = Color(editedNote.color),
            onColorSelected = { color ->
                editedNote = editedNote.copy(color = color.toArgb())
                showColorPicker = false
            },
            onDismiss = { showColorPicker = false }
        )
    }
}

@Composable
private fun NoteTitleField(
    title: String,
    onTitleChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = title,
        onValueChange = onTitleChange,
        label = { Text("Название заметки") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
    )
}

@Composable
private fun NoteContentField(
    content: String,
    onContentChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = content,
        onValueChange = onContentChange,
        label = { Text("Текст заметки") },
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 150.dp),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default),
        maxLines = Int.MAX_VALUE
    )
}

@Composable
private fun SelfDestructSection(
    hasSelfDestruct: Boolean,
    selfDestructDate: LocalDate?,
    onSelfDestructChange: (Boolean) -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    showDatePicker: Boolean,
    onShowDatePicker: (Boolean) -> Unit,
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Добавить дату самоуничтожения",
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = hasSelfDestruct,
                onCheckedChange = onSelfDestructChange
            )
        }

        if (hasSelfDestruct) {
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { onShowDatePicker(true) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.DateRange, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = selfDestructDate?.toString() ?: "Выберите дату")
            }
        }
    }
}

@Composable
private fun ColorSelectionSection(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit,
    showColorPicker: Boolean,
    onShowColorPicker: (Boolean) -> Unit,
) {
    Column {
        Text(text = "Цвет заметки:", style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(8.dp))

        val defaultColors = listOf(
            Color.Red,
            Color.Green,
            Color.Blue,
            Color.Yellow,
            Color.Cyan,
            Color.Magenta,
            Color.White,
            Color.Black
        )

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            defaultColors.forEach { color ->
                ColorSelectionItem(
                    color = color,
                    isSelected = color == selectedColor,
                    onColorSelected = { onColorSelected(color) }
                )
            }

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.White)
                    .border(1.dp, Color.Black, RoundedCornerShape(4.dp))
                    .clickable { onShowColorPicker(true) }
                    .pointerInput(Unit) {
                        detectDragGestures { _, _ ->
                            onShowColorPicker(true)
                        }
                    }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Red,
                                    Color.Yellow,
                                    Color.Green,
                                    Color.Cyan,
                                    Color.Blue,
                                    Color.Magenta
                                )
                            )
                        )
                )
                if (defaultColors.none { it == selectedColor }) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
private fun ColorSelectionItem(
    color: Color,
    isSelected: Boolean,
    onColorSelected: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(color)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) Color.Black else Color.Gray,
                shape = RoundedCornerShape(4.dp)
            )
            .clickable(onClick = onColorSelected)
    ) {
        if (isSelected) {
            Icon(
                Icons.Default.Check,
                contentDescription = null,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
private fun ImportanceSelectionSection(
    importance: Importance,
    onImportanceSelected: (Importance) -> Unit,
) {
    Column {
        Text(text = "Важность:", style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(8.dp))

        Importance.entries.forEach { item ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onImportanceSelected(item) }
            ) {
                Checkbox(
                    checked = importance == item,
                    onCheckedChange = { onImportanceSelected(item) }
                )
                Text(
                    text = when (item) {
                        Importance.LOW -> "\uD83D\uDE34 Неважная"
                        Importance.NORMAL -> "\uD83D\uDE4F Обычная"
                        Importance.HIGH -> "❗\uFE0F Сверхважная"
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerDialog(
    onDismiss: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
) {
    val datePickerState = remember {
        DatePickerState(
            yearRange = YearMonth.now().year..(YearMonth.now().year + 10),
            initialSelectedDateMillis = System.currentTimeMillis(),
            initialDisplayMode = DisplayMode.Picker,
            locale = CalendarLocale.getDefault()
        )
    }

    androidx.compose.material3.DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let {
                        onDateSelected(
                            Instant.ofEpochMilli(it)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                        )
                    }
                    onDismiss()
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}