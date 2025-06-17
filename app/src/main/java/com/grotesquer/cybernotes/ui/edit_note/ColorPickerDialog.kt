package com.grotesquer.cybernotes.ui.edit_note

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

@Composable
fun ColorPickerDialog(
    initialColor: Color,
    onColorSelected: (Color) -> Unit,
    onDismiss: () -> Unit,
) {
    var colorState by remember {
        mutableStateOf(ColorPickerState(initialColor, 1f))
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                onColorSelected(colorState.toFinalColor())
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        text = {
            ColorPickerContent(
                colorState = colorState,
                onColorChange = { colorState = it },
                modifier = Modifier.padding(16.dp)
            )
        }
    )
}

@Composable
private fun ColorPickerContent(
    colorState: ColorPickerState,
    onColorChange: (ColorPickerState) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ColorPreviewWithSlider(
            color = colorState.baseColor,
            brightness = colorState.brightness,
            onBrightnessChange = { onColorChange(colorState.copy(brightness = it)) }
        )
        ColorSpectrumPicker(
            selectedColor = colorState.baseColor,
            onColorSelected = { onColorChange(colorState.copy(baseColor = it)) }
        )
    }
}

@Composable
private fun ColorPreviewWithSlider(
    color: Color,
    brightness: Float,
    onBrightnessChange: (Float) -> Unit,
) {
    val adjustedColor = remember(color, brightness) {
        val hsv = FloatArray(3)
        android.graphics.Color.colorToHSV(color.toArgb(), hsv)
        hsv[2] = brightness.coerceIn(0f, 1f)
        Color(android.graphics.Color.HSVToColor(hsv))
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(adjustedColor)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Slider(
            value = brightness,
            onValueChange = onBrightnessChange,
            valueRange = 0.2f..1f,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ColorSpectrumPicker(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit,
) {
    var selectorPosition by remember { mutableFloatStateOf(0f) }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        selectorPosition = offset.x.coerceIn(0f, size.width.toFloat())
                        updateColorFromPosition(
                            selectorPosition,
                            size.width.toFloat(),
                            onColorSelected
                        )
                    },
                    onDrag = { change, _ ->
                        selectorPosition = change.position.x.coerceIn(0f, size.width.toFloat())
                        updateColorFromPosition(
                            selectorPosition,
                            size.width.toFloat(),
                            onColorSelected
                        )
                    }
                )
            }
    ) {
        val width = constraints.maxWidth.toFloat()

        LaunchedEffect(selectedColor) {
            val hsv = FloatArray(3)
            android.graphics.Color.colorToHSV(selectedColor.toArgb(), hsv)
            selectorPosition = (hsv[0] / 360f) * width
        }

        ColorSpectrumCanvas()

        CrosshairIndicator(position = selectorPosition)
    }
}

private fun updateColorFromPosition(x: Float, width: Float, onColorSelected: (Color) -> Unit) {
    val hue = (x / width) * 360f
    onColorSelected(Color.hsv(hue, 1f, 1f))
}

@Composable
private fun ColorSpectrumCanvas() {
    val spectrumColors = remember {
        listOf(
            Color.Red,
            Color.Yellow,
            Color.Green,
            Color.Cyan,
            Color.Blue,
            Color.Magenta,
            Color.Red
        )
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(
            brush = Brush.horizontalGradient(
                colors = spectrumColors,
                tileMode = TileMode.Clamp
            )
        )
    }
}

@Composable
private fun CrosshairIndicator(position: Float) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val indicatorSize = 12.dp.toPx()
        val centerY = size.height / 2
        val centerX = position

        val crossSize = indicatorSize * 0.6f
        drawLine(
            color = Color.White,
            start = Offset(centerX - crossSize, centerY),
            end = Offset(centerX + crossSize, centerY),
            strokeWidth = 3f
        )
        drawLine(
            color = Color.White,
            start = Offset(centerX, centerY - crossSize),
            end = Offset(centerX, centerY + crossSize),
            strokeWidth = 3f
        )
    }
}

private data class ColorPickerState(
    val baseColor: Color,
    val brightness: Float,
) {
    fun toFinalColor(): Color {
        val hsv = FloatArray(3)
        android.graphics.Color.colorToHSV(baseColor.toArgb(), hsv)
        hsv[2] = brightness.coerceIn(0f, 1f)
        return Color(android.graphics.Color.HSVToColor(hsv))
    }
}