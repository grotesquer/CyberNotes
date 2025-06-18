package com.grotesquer.cybernotes.ui.edit_note

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
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.grotesquer.cybernotes.di.AppViewModelProvider
import com.grotesquer.cybernotes.model.Importance
import com.grotesquer.cybernotes.ui.elements.ColorPickerDialog
import com.grotesquer.cybernotes.ui.elements.ImportanceIndicator
import com.grotesquer.cybernotes.ui.elements.MatrixScreen
import com.grotesquer.cybernotes.ui.theme.matrixGreen
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditScreen(
    viewModel: NoteEditViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onBack: () -> Unit,
) {
    val state = viewModel.state

    LaunchedEffect(key1 = Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                NoteEditEffect.NavigateBack -> onBack()
                is NoteEditEffect.ShowError -> {}
            }
        }
    }

    MatrixScreen(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { viewModel.handleEvent(NoteEditEvent.Cancel) }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    TextButton(onClick = { viewModel.handleEvent(NoteEditEvent.SaveNote) }) {
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            MatrixTextField(
                value = state.note.title,
                onValueChange = { viewModel.handleEvent(NoteEditEvent.UpdateTitle(it)) },
                label = "НАЗВАНИЕ",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            MatrixTextField(
                value = state.note.content,
                onValueChange = { viewModel.handleEvent(NoteEditEvent.UpdateContent(it)) },
                label = "СОДЕРЖАНИЕ",
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 150.dp),
                maxLines = Int.MAX_VALUE
            )

            Spacer(modifier = Modifier.height(24.dp))

            SelfDestructSection(
                hasSelfDestruct = state.note.selfDestructDate != null,
                selfDestructDate = state.note.selfDestructDate,
                onSelfDestructChange = {
                    viewModel.handleEvent(
                        NoteEditEvent.UpdateSelfDestruct(
                            it
                        )
                    )
                },
                onDateSelected = { date ->
                    viewModel.handleEvent(NoteEditEvent.UpdateSelfDestructDate(date))
                    viewModel.handleEvent(NoteEditEvent.HideDatePicker)
                },
                showDatePicker = state.showDatePicker,
                onShowDatePicker = { show ->
                    if (show) viewModel.handleEvent(NoteEditEvent.ShowDatePicker)
                    else viewModel.handleEvent(NoteEditEvent.HideDatePicker)
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            ColorSelectionSection(
                selectedColor = Color(state.note.color),
                onColorSelected = { color ->
                    viewModel.handleEvent(NoteEditEvent.UpdateColor(color.toArgb()))
                    viewModel.handleEvent(NoteEditEvent.HideColorPicker)
                },
                showColorPicker = state.showColorPicker,
                onShowColorPicker = { show ->
                    if (show) viewModel.handleEvent(NoteEditEvent.ShowColorPicker)
                    else viewModel.handleEvent(NoteEditEvent.HideColorPicker)
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            ImportanceSelectionSection(
                importance = state.note.importance,
                onImportanceSelected = { importance ->
                    viewModel.handleEvent(NoteEditEvent.UpdateImportance(importance))
                }
            )
        }
    }

    if (state.showDatePicker) {
        DatePickerDialog(
            onDismiss = { viewModel.handleEvent(NoteEditEvent.HideDatePicker) },
            onDateSelected = { date ->
                viewModel.handleEvent(NoteEditEvent.UpdateSelfDestructDate(date))
            }
        )
    }

    if (state.showColorPicker) {
        ColorPickerDialog(
            initialColor = Color(state.note.color),
            onColorSelected = { color ->
                viewModel.handleEvent(NoteEditEvent.UpdateColor(color.toArgb()))
            },
            onDismiss = { viewModel.handleEvent(NoteEditEvent.HideColorPicker) }
        )
    }
}

@Composable
private fun MatrixTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    maxLines: Int = 1,
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
        DatePicker(state = datePickerState)
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