package com.grotesquer.cybernotes.ui.elements

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.grotesquer.cybernotes.ui.theme.matrixGreen

@Composable
fun MatrixScreen(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    val scanLinePosition by rememberScanLineEffect()

    Box(modifier = modifier.matrixBackground(scanLinePosition)) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = topBar,
            floatingActionButton = floatingActionButton,
            content = content
        )
    }
}

@Composable
fun rememberScanLineEffect(): State<Float> {
    val infiniteTransition = rememberInfiniteTransition()
    return infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
}

fun Modifier.matrixBackground(scanLinePosition: Float): Modifier = this
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
