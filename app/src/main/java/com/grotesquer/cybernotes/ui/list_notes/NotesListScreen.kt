package com.grotesquer.cybernotes.ui.list_notes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grotesquer.cybernotes.model.Importance
import com.grotesquer.cybernotes.model.Note
import com.grotesquer.cybernotes.ui.theme.matrixGreen

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NotesListScreen(
    onNoteClick: (Note) -> Unit,
    onAddNote: () -> Unit
) {
    val notes = remember {
        listOf(
            Note.create(
                title = "System Alert",
                content = "Neural network anomaly detected in sector 7",
                importance = Importance.HIGH
            ),
            Note.create(
                title = "Data Stream",
                content = "Monitoring encrypted channels...",
                importance = Importance.NORMAL
            ),
            Note.create(
                title = "Code Fragment",
                content = "01001000 01101001 00100000 01010100 01101000 01100101 01110010 01100101",
                importance = Importance.LOW
            ),
            Note.create(
                title = "Security Breach",
                content = "Unauthorized access attempt from external node",
                importance = Importance.HIGH
            ),
            Note.create(
                title = "Maintenance Log",
                content = "System diagnostics running... All circuits nominal",
                importance = Importance.NORMAL
            )
        )
    }

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
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onAddNote,
                    containerColor = matrixGreen,
                    contentColor = Color.Black
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add note")
                }
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(notes, key = { it.uid }) { note ->
                    MatrixNoteItem(
                        note = note,
                        onClick = { onNoteClick(note) },
                        modifier = Modifier.animateItemPlacement()
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MatrixNoteItem(
    note: Note,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Black.copy(alpha = 0.7f))
            .border(
                width = 1.dp,
                color = matrixGreen.copy(alpha = 0.5f),
                shape = RoundedCornerShape(8.dp)
            )
            .combinedClickable(
                onClick = onClick,
                onLongClick = { isExpanded = !isExpanded }
            )
            .padding(12.dp)
    ) {
        Column {
            MatrixText(
                text = note.title,
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                MatrixText(
                    text = note.content,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(matrixGreen.copy(alpha = 0.3f))
                    .padding(top = 8.dp)
            )

            Row(
                modifier = Modifier.padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ImportanceIndicator(importance = note.importance)
                Spacer(modifier = Modifier.width(8.dp))
                MatrixText(
                    text = when (note.importance) {
                        Importance.HIGH -> "CRITICAL"
                        Importance.NORMAL -> "STANDARD"
                        Importance.LOW -> "LOW PRIORITY"
                    },
                    fontSize = 12.sp,
                    alpha = 0.7f
                )
            }
        }
    }
}

@Composable
private fun MatrixText(
    text: String,
    fontSize: androidx.compose.ui.unit.TextUnit,
    modifier: Modifier = Modifier,
    alpha: Float = 1f
) {
    val infiniteTransition = rememberInfiniteTransition()
    val glitchOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Text(
        text = buildAnnotatedString {
            text.forEachIndexed { index, c ->
                val randomOffset = (glitchOffset * (index % 3)).toInt()
                val glitchChar = when {
                    randomOffset == 1 -> (c.code + 1).toChar()
                    randomOffset == 2 -> (c.code - 1).toChar()
                    else -> c
                }
                withStyle(
                    SpanStyle(
                        color = matrixGreen.copy(alpha = alpha),
                        fontSize = fontSize,
                        textDecoration = if (index % 10 == 0) TextDecoration.Underline else TextDecoration.None
                    )
                ) {
                    append(glitchChar.toString())
                }
            }
        },
        modifier = modifier,
        fontFamily = FontFamily.Monospace
    )
}

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