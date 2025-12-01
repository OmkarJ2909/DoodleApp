package com.example.doodleapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.doodleapp.ui.theme.DoodleAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DoodleAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DoodleScreen()
                }
            }
        }
    }
}

// One stroke drawn by the user
data class DoodleStroke(
    val points: List<Offset>,
    val color: Color,
    val strokeWidth: Float
)

@Composable
fun DoodleScreen() {
    // Brush controls
    var brushSize by remember { mutableStateOf(20f) }
    var selectedColor by remember { mutableStateOf(Color.Black) }

    // Drawing state
    val strokes = remember { mutableStateListOf<DoodleStroke>() }   // finished strokes
    var currentStroke by remember { mutableStateOf<DoodleStroke?>(null) } // in-progress stroke

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        ToolPanel(
            brushSize = brushSize,
            onBrushSizeChange = { brushSize = it },
            selectedColor = selectedColor,
            onColorSelected = { selectedColor = it },
            onClearCanvas = {
                strokes.clear()
                currentStroke = null
            },
            onUndo = {
                if (strokes.isNotEmpty()) {
                    strokes.removeAt(strokes.lastIndex)   // works on all APIs
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Drawing area
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE0E0E0))
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(brushSize, selectedColor) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                currentStroke = DoodleStroke(
                                    points = listOf(offset),
                                    color = selectedColor,
                                    strokeWidth = brushSize
                                )
                            },
                            onDrag = { change, _ ->
                                val newPoint = change.position
                                val stroke = currentStroke
                                if (stroke != null) {
                                    currentStroke = stroke.copy(
                                        points = stroke.points + newPoint
                                    )
                                }
                            },
                            onDragEnd = {
                                currentStroke?.let { strokes.add(it) }
                                currentStroke = null
                            },
                            onDragCancel = {
                                currentStroke = null
                            }
                        )
                    }
            ) {
                // Draw finished strokes
                for (stroke in strokes) {
                    drawStroke(stroke)
                }
                // Draw the one being drawn
                currentStroke?.let { drawStroke(it) }
            }
        }
    }
}

@Composable
fun ToolPanel(
    brushSize: Float,
    onBrushSizeChange: (Float) -> Unit,
    selectedColor: Color,
    onColorSelected: (Color) -> Unit,
    onClearCanvas: () -> Unit,
    onUndo: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = "Brush size: ${brushSize.toInt()}")

        Slider(
            value = brushSize,
            onValueChange = onBrushSizeChange,
            valueRange = 5f..60f,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Brush color:")

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ColorSwatch(
                color = Color.Black,
                isSelected = selectedColor == Color.Black,
                onClick = { onColorSelected(Color.Black) }
            )
            ColorSwatch(
                color = Color.Red,
                isSelected = selectedColor == Color.Red,
                onClick = { onColorSelected(Color.Red) }
            )
            ColorSwatch(
                color = Color.Blue,
                isSelected = selectedColor == Color.Blue,
                onClick = { onColorSelected(Color.Blue) }
            )
            ColorSwatch(
                color = Color(0xFF2E7D32),
                isSelected = selectedColor == Color(0xFF2E7D32),
                onClick = { onColorSelected(Color(0xFF2E7D32)) }
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(onClick = onUndo) {
                Text("Undo")
            }

            Button(onClick = onClearCanvas) {
                Text("Clear")
            }
        }
    }
}

@Composable
fun ColorSwatch(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(if (isSelected) 32.dp else 28.dp)
            .padding(2.dp)
            .background(if (isSelected) Color.White else Color.Transparent)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color)
        )
    }
}

// Helper: draw one stroke
private fun DrawScope.drawStroke(stroke: DoodleStroke) {
    if (stroke.points.size < 2) return

    val path = Path().apply {
        moveTo(stroke.points.first().x, stroke.points.first().y)
        for (p in stroke.points.drop(1)) {
            lineTo(p.x, p.y)
        }
    }

    drawPath(
        path = path,
        color = stroke.color,
        style = Stroke(
            width = stroke.strokeWidth,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )
}
