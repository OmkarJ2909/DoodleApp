# DoodleApp – IA08 Technical I: Doodle

This is a simple doodle application built for Android using **Jetpack Compose**.  
It was created for the IA08 Technical I assignment to demonstrate UI design, canvas drawing, touch interactions, and modern Android development practices.

---

## 🎨 Features

### ✔ Drawing Canvas
- Draw freehand using touch or mouse
- Smooth stroke rendering using `Canvas` and `Path`
- Redraws every stroke stored in state

### ✔ Brush Controls (Tool Panel)
- **Brush Size Slider** – adjust stroke thickness from 5–60 dp
- **Color Selection** – choose from Black, Red, Blue, and Green
- **Clear Button** – instantly clears the entire canvas

### ⭐ Bonus Feature
- **Undo Button** – removes the most recent stroke  
  (Counts as the bonus requirement for the assignment)

---

## 🛠️ Implementation Details

### Compose & State
- UI built entirely using Kotlin + Jetpack Compose (no XML)
- Brush size, selected color, strokes list, and current stroke stored using Compose state (`mutableStateOf` + `mutableStateListOf`)

### Drawing Logic
- Touch input handled using `pointerInput` + `detectDragGestures`
- Each stroke stored as:
  ```kotlin
  data class DoodleStroke(
      val points: List<Offset>,
      val color: Color,
      val strokeWidth: Float
  )
