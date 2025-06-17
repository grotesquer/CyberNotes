package com.grotesquer.cybernotes.ui.elements

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

@Composable
fun ColorPickerDialog(
    initialColor: Color,
    onColorSelected: (Color) -> Unit,
    onDismiss: () -> Unit,
) {
    val initialHsv = FloatArray(3).apply {
        android.graphics.Color.colorToHSV(initialColor.toArgb(), this)
    }
    var colorState by remember {
        mutableStateOf(
            ColorPickerState(
                baseColor = Color.hsv(initialHsv[0], initialHsv[1], 1f),
                brightness = initialHsv[2]
            )
        )
    }
    var showSpectrum by remember { mutableStateOf(true) }
    val transition = updateTransition(showSpectrum, label = "spectrumTransition")

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
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ColorPreviewWithSlider(
                    color = colorState.baseColor,
                    brightness = colorState.brightness,
                    onBrightnessChange = { newBrightness ->
                        colorState = colorState.copy(brightness = newBrightness)
                    }
                )

                transition.AnimatedContent(
                    transitionSpec = {
                        fadeIn(animationSpec = tween(150)) togetherWith fadeOut(animationSpec = tween(150))
                    }
                ) { showSpectrum ->
                    if (showSpectrum) {
                        ColorSpectrumPicker(
                            selectedColor = colorState.baseColor,
                            onColorSelected = { newColor ->
                                val hsv = FloatArray(3)
                                android.graphics.Color.colorToHSV(newColor.toArgb(), hsv)
                                colorState = colorState.copy(
                                    baseColor = Color.hsv(hsv[0], hsv[1], 1f)
                                )
                            }
                        )
                    } else {
                        CustomColorGradient(
                            color = colorState.baseColor,
                            onColorSelected = { newColor ->
                                colorState = colorState.copy(baseColor = newColor)
                            }
                        )
                    }
                }

                Button(
                    onClick = { showSpectrum = !showSpectrum },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (showSpectrum) "Use Custom Gradient" else "Show Spectrum")
                }
            }
        }
    )
}

@Composable
private fun CustomColorGradient(
    color: Color,
    onColorSelected: (Color) -> Unit
) {
    var selectorPosition by remember { mutableFloatStateOf(0.5f) }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        selectorPosition = (offset.x / size.width).coerceIn(0f, 1f)
                        updateCustomColor(selectorPosition, color, onColorSelected)
                    },
                    onDrag = { change, _ ->
                        selectorPosition = (change.position.x / size.width).coerceIn(0f, 1f)
                        updateCustomColor(selectorPosition, color, onColorSelected)
                    }
                )
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        color.copy(alpha = 0.2f),
                        color
                    )
                )
            )
        }

        CrosshairIndicator(position = selectorPosition * constraints.maxWidth.toFloat())
    }
}

private fun updateCustomColor(position: Float, baseColor: Color, onColorSelected: (Color) -> Unit) {
    val alpha = position.coerceIn(0f, 1f)
    onColorSelected(baseColor.copy(alpha = alpha))
}

@Composable
private fun ColorPreviewWithSlider(
    color: Color,
    brightness: Float,
    onBrightnessChange: (Float) -> Unit,
) {
    val adjustedColor by remember(color, brightness) {
        derivedStateOf {
            val hsv = FloatArray(3)
            android.graphics.Color.colorToHSV(color.toArgb(), hsv)
            hsv[2] = brightness.coerceIn(0f, 1f)
            Color(android.graphics.Color.HSVToColor(hsv))
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimatedContent(
            targetState = adjustedColor,
            transitionSpec = {
                (fadeIn(animationSpec = tween(150)) + scaleIn()).togetherWith(
                    fadeOut(
                        animationSpec = tween(
                            150
                        )
                    )
                )
            }
        ) { currentColor ->
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(currentColor)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Slider(
            value = brightness,
            onValueChange = { newBrightness ->
                onBrightnessChange(newBrightness.coerceIn(0.2f, 1f))
            },
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
                        updateColorFromPosition(selectorPosition, size.width.toFloat(), onColorSelected)
                    },
                    onDrag = { change, _ ->
                        selectorPosition = change.position.x.coerceIn(0f, size.width.toFloat())
                        updateColorFromPosition(selectorPosition, size.width.toFloat(), onColorSelected)
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