package com.grotesquer.cybernotes.ui.elements

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.grotesquer.cybernotes.model.Importance
import com.grotesquer.cybernotes.ui.theme.matrixGreen

@Composable
fun ImportanceIndicator(importance: Importance) {
    val color = when (importance) {
        Importance.HIGH -> Color.Red
        Importance.NORMAL -> matrixGreen
        Importance.LOW -> Color(0xFF666666)
    }
    val pulseColor by rememberInfiniteTransition().animateColor(
        initialValue = color.copy(alpha = 0.3f),
        targetValue = color,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Canvas(modifier = Modifier.size(10.dp)) {
        drawCircle(
            color = pulseColor,
            radius = size.minDimension / 2,
            style = Stroke(width = 2.dp.toPx())
        )
        drawCircle(
            color = color,
            radius = size.minDimension / 3
        )
    }
}