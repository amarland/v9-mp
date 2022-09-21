package com.amarland.v9mp.demo.common

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.amarland.v9mp.PathSegmentType
import com.amarland.v9mp.Point
import com.amarland.v9mp.Slices
import com.amarland.v9mp.segments
import com.amarland.v9mp.slice

@Composable
fun Demo() {
    MaterialTheme(
        colors = darkColors(
            primary = Color(0xFF2196F3),
            secondary = Color(0xFF3DDC84),
            surface = Color(0xFF073042)
        )
    ) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.padding(vertical = 16.dp)) {
                var pathWidth by remember { mutableStateOf(68.0f) }
                var pathHeight by remember { mutableStateOf(32.0f) }

                val path = remember {
                    Path().apply {
                        moveTo(20.0f, 2.0f)
                        lineTo(4.0f, 2.0f)
                        relativeCubicTo(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
                        relativeLineTo(0.0f, 18.0f)
                        relativeLineTo(4.0f, -4.0f)
                        relativeLineTo(14.0f, 0.0f)
                        relativeCubicTo(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
                        lineTo(22.0f, 4.0f)
                        relativeCubicTo(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
                        close()
                    }
                }
                val slices = remember { Slices(9.0f, 7.0f, 15.0f, 13.0f) }
                val resizablePath = remember(path) { path.slice(slices) }
                val resizedPath = remember(resizablePath, pathWidth, pathHeight) {
                    resizablePath.resize(pathWidth, pathHeight)
                }

                ResizablePathViewer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.5f),
                    path,
                    resizedPath,
                    slices
                )
                Spacer(modifier = Modifier.weight(1.0f))
                DimensionSlider(
                    label = "Width ", // blank space is intentional
                    value = pathWidth,
                    onValueChange = { pathWidth = it },
                    valueRange = resizablePath.bounds.width..68.0f
                )
                DimensionSlider(
                    label = "Height",
                    value = pathHeight,
                    onValueChange = { pathHeight = it },
                    valueRange = resizablePath.bounds.height..48.0f
                )
            }
        }
    }
}

@Composable
private fun ResizablePathViewer(
    modifier: Modifier = Modifier,
    path: Path,
    resizedPath: Path,
    slices: Slices
) {
    val pathColor = SolidColor(MaterialTheme.colors.secondary)
    val accent1Color = SolidColor(MaterialTheme.colors.primary)
    val accent1FillColor = SolidColor(accent1Color.value.copy(alpha = 0.1f))
    val accent2Color = SolidColor(MaterialTheme.colors.onSurface)
    val accent2FillColor = SolidColor(accent2Color.value.copy(alpha = 0.1f))
    val dashEffect = PathEffect.dashPathEffect(floatArrayOf(1.0f, 0.5f))

    val pathSize = 24.0f
    val strokeWidth = 0.25f

    Canvas(modifier) {
        scale(size.width / 73.7f, pivot = Offset.Zero) {
            drawPath(path, brush = pathColor)

            translate(left = pathSize) {
                drawPath(path, brush = pathColor)
                drawPathControls(path, accent1Color, strokeWidth)
            }

            translate(left = pathSize * 2.0f) {
                drawPath(path, brush = pathColor)

                for (slice in slices.horizontalSlices) {
                    if (slice.size > 0) {
                        drawLine(
                            brush = accent2Color,
                            start = Offset(0.0f, slice.start),
                            end = Offset(pathSize, slice.start),
                            strokeWidth = strokeWidth,
                            pathEffect = dashEffect
                        )
                        drawLine(
                            brush = accent2Color,
                            start = Offset(0.0f, slice.end),
                            end = Offset(pathSize, slice.end),
                            strokeWidth = strokeWidth,
                            pathEffect = dashEffect
                        )
                        drawRect(
                            brush = accent2FillColor,
                            topLeft = Offset(0.0f, slice.start),
                            size = Size(pathSize, slice.end - slice.start)
                        )
                    }
                }

                for (slice in slices.verticalSlices) {
                    if (slice.size > 0) {
                        drawLine(
                            brush = accent1Color,
                            start = Offset(slice.start, 0.0f),
                            end = Offset(slice.start, pathSize),
                            strokeWidth = strokeWidth,
                            pathEffect = dashEffect
                        )
                        drawLine(
                            brush = accent1Color,
                            start = Offset(slice.end, 0.0f),
                            end = Offset(slice.end, pathSize),
                            strokeWidth = strokeWidth,
                            pathEffect = dashEffect
                        )
                        drawRect(
                            brush = accent1FillColor,
                            topLeft = Offset(slice.start, 0.0f),
                            size = Size(slice.end - slice.start, pathSize)
                        )
                    }
                }
            }

            translate(top = pathSize + 4.0f) {
                drawPath(resizedPath, brush = pathColor)
            }
        }
    }
}

@Composable
private fun DimensionSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>
) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontFamily = FontFamily.Monospace)
        Slider(
            modifier = Modifier.weight(1.0f),
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = ((valueRange.endInclusive - valueRange.start) / 4).toInt() - 1
        )
    }
}

private fun DrawScope.drawPathControls(
    path: Path,
    accentColor: SolidColor,
    strokeWidth: Float
) {
    for (segment in path.segments()) {
        when (segment.type) {
            PathSegmentType.QUADRATIC -> {
                drawLine(
                    brush = accentColor,
                    start = segment.points[0].toOffset(),
                    end = segment.points[1].toOffset(),
                    strokeWidth = strokeWidth
                )
                drawLine(
                    brush = accentColor,
                    start = segment.points[1].toOffset(),
                    end = segment.points[2].toOffset(),
                    strokeWidth = strokeWidth
                )
            }

            PathSegmentType.CUBIC -> {
                drawLine(
                    brush = accentColor,
                    start = segment.points[0].toOffset(),
                    end = segment.points[1].toOffset(),
                    strokeWidth = strokeWidth
                )
                drawLine(
                    brush = accentColor,
                    start = segment.points[2].toOffset(),
                    end = segment.points[3].toOffset(),
                    strokeWidth = strokeWidth
                )
            }

            else -> {}
        }

        for (point in segment.points) {
            drawCircle(
                brush = accentColor,
                radius = 0.4f,
                center = point.toOffset()
            )
        }
    }
}

private fun Point.toOffset() = Offset(x, y)
