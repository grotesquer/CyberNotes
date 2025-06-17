package com.grotesquer.cybernotes.ui.edit_note

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CalendarLocale
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.grotesquer.cybernotes.model.Importance
import com.grotesquer.cybernotes.model.Note
import com.grotesquer.cybernotes.ui.elements.ColorPickerDialog
import com.grotesquer.cybernotes.ui.elements.ImportanceIndicator
import com.grotesquer.cybernotes.ui.theme.matrixGreen
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

    val infiniteTransition = rememberInfiniteTransition()
    val scanLinePosition by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .drawWithCache {
                val gradient = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        matrixGreen.copy(alpha = 0.1f),
                        Color.Transparent
                    ),
                    startY = size.height * scanLinePosition - 100f,
                    endY = size.height * scanLinePosition + 100f
                )
                onDrawBehind {
                    drawRect(gradient)
                }
            }
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {},
                    navigationIcon = {
                        TextButton(onClick = { onCancel() }) {
                            Text("ОТМЕНА", color = Color.Red)
                        }
                    },
                    actions = {
                        TextButton(onClick = { onSave(editedNote) }) {
                            Text("СОХРАНИТЬ", color = matrixGreen)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Black.copy(alpha = 0.8f)
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp)
            ) {
                MatrixTextField(
                    value = editedNote.title,
                    onValueChange = { editedNote = editedNote.copy(title = it) },
                    label = "НАЗВАНИЕ",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                MatrixTextField(
                    value = editedNote.content,
                    onValueChange = { editedNote = editedNote.copy(content = it) },
                    label = "СОДЕРЖАНИЕ",
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 150.dp),
                    maxLines = Int.MAX_VALUE
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
private fun MatrixTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    maxLines: Int = 1
) {
    var isFocused by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                text = label,
                color = matrixGreen.copy(alpha = 0.7f),
                fontFamily = FontFamily.Monospace
            )
        },
        modifier = modifier
            .onFocusChanged { isFocused = it.isFocused },
        textStyle = MaterialTheme.typography.bodyMedium.copy(
            color = matrixGreen,
            fontFamily = FontFamily.Monospace
        ),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Black.copy(alpha = 0.5f),
            unfocusedContainerColor = Color.Black.copy(alpha = 0.3f),
            focusedTextColor = matrixGreen,
            unfocusedTextColor = matrixGreen.copy(alpha = 0.8f),
            cursorColor = matrixGreen,
            focusedIndicatorColor = matrixGreen,
            unfocusedIndicatorColor = matrixGreen.copy(alpha = 0.5f),
            focusedLabelColor = matrixGreen,
            unfocusedLabelColor = matrixGreen.copy(alpha = 0.5f)
        ),
        singleLine = maxLines == 1,
        maxLines = maxLines,
        keyboardOptions = KeyboardOptions(
            imeAction = if (maxLines == 1) ImeAction.Next else ImeAction.Default
        )
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
                text = "САМОУНИЧТОЖЕНИЕ",
                color = matrixGreen,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = hasSelfDestruct,
                onCheckedChange = onSelfDestructChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = matrixGreen,
                    checkedTrackColor = matrixGreen.copy(alpha = 0.5f),
                    uncheckedThumbColor = Color.Gray,
                    uncheckedTrackColor = Color.DarkGray
                )
            )
        }

        if (hasSelfDestruct) {
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { onShowDatePicker(true) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = matrixGreen
                ),
                border = BorderStroke(1.dp, matrixGreen)
            ) {
                Icon(Icons.Default.DateRange, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = selfDestructDate?.toString() ?: "ВЫБЕРИТЕ ДАТУ",
                    fontFamily = FontFamily.Monospace
                )
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
        Text(
            text = "ЦВЕТ ЗАМЕТКИ:",
            color = matrixGreen,
            fontFamily = FontFamily.Monospace,
            style = MaterialTheme.typography.labelLarge
        )
        Spacer(modifier = Modifier.height(8.dp))

        val defaultColors = listOf(
            Color.Red,
            matrixGreen,
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
                MatrixColorSelectionItem(
                    color = color,
                    isSelected = color == selectedColor,
                    onColorSelected = { onColorSelected(color) }
                )
            }

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Black)
                    .border(1.dp, matrixGreen, RoundedCornerShape(4.dp))
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
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Red,
                                    Color.Yellow,
                                    matrixGreen,
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
                        tint = matrixGreen,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
private fun MatrixColorSelectionItem(
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
                color = if (isSelected) matrixGreen else Color.Gray,
                shape = RoundedCornerShape(4.dp)
            )
            .clickable(onClick = onColorSelected)
    ) {
        if (isSelected) {
            Icon(
                Icons.Default.Check,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.align(Alignment.Center)
            )
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
                },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = matrixGreen
                )
            ) {
                Text("ПОДТВЕРДИТЬ", fontFamily = FontFamily.Monospace)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = matrixGreen
                )
            ) {
                Text("ОТМЕНА", fontFamily = FontFamily.Monospace)
            }
        },
    ) {
        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                containerColor = Color.Black,
                titleContentColor = matrixGreen,
                headlineContentColor = matrixGreen,
                weekdayContentColor = matrixGreen,
                subheadContentColor = matrixGreen,
                navigationContentColor = matrixGreen,
                yearContentColor = matrixGreen,
                currentYearContentColor = matrixGreen,
                selectedYearContentColor = Color.Black,
                selectedYearContainerColor = matrixGreen,
                dayContentColor = matrixGreen,
                disabledDayContentColor = matrixGreen.copy(alpha = 0.3f),
                selectedDayContentColor = Color.Black,
                selectedDayContainerColor = matrixGreen,
                disabledSelectedDayContainerColor = matrixGreen.copy(alpha = 0.3f),
                todayContentColor = matrixGreen,
                todayDateBorderColor = matrixGreen
            )
        )
    }
}

@Composable
private fun ImportanceSelectionSection(
    importance: Importance,
    onImportanceSelected: (Importance) -> Unit,
) {
    Column {
        Text(
            text = "Важность:",
            style = MaterialTheme.typography.labelLarge,
            color = matrixGreen
        )
        Spacer(modifier = Modifier.height(8.dp))

        Importance.entries.forEach { item ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onImportanceSelected(item) }
                    .padding(vertical = 4.dp)
            ) {
                ImportanceIndicator(importance = item)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = when (item) {
                        Importance.LOW -> "Неважная"
                        Importance.NORMAL -> "Обычная"
                        Importance.HIGH -> "Сверхважная"
                    },
                    color = matrixGreen,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.weight(1f)
                )
                RadioButton(
                    selected = importance == item,
                    onClick = { onImportanceSelected(item) },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = matrixGreen,
                        unselectedColor = Color.Gray
                    )
                )
            }
        }
    }
}