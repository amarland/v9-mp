/*
 * Copyright 2022 Anthony Marland
 * Copyright 2022 Romain Guy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.amarland.v9mp

import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Path
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.Shape
import java.awt.geom.Path2D
import java.awt.image.BufferedImage
import java.awt.image.DataBufferInt
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PathSegmentsTest {

    @Test
    fun convertedConics() {
        val path = Path().apply {
            addRoundRect(RoundRect(0.0f, 0.0f, 52.0f, 52.0f, 12.0f, 12.0f))
        }
        val shape1 = Path2D.Float().apply {
            moveTo(0.0f, 40.0f)
            quadTo(0.0f, 45.0f, 3.51f, 48.48f)
            quadTo(7.02f, 52.0f, 12.0f, 52.0f)
            lineTo(40.0f, 52.0f)
            quadTo(45.0f, 52.0f, 48.48f, 48.48f)
            quadTo(52.0f, 45.0f, 52.0f, 40.0f)
            lineTo(52.0f, 12.0f)
            quadTo(52.0f, 7.02f, 48.48f, 3.51f)
            quadTo(45.0f, 0.0f, 40.0f, 0.0f)
            lineTo(12.0f, 0.0f)
            quadTo(7.02f, 0.0f, 3.51f, 3.51f)
            quadTo(0.0f, 7.02f, 0.0f, 12.0f)
            quadTo(0.0f, 12.0f, 0.0f, 40.0f)
        }
        val shape2 = Path2D.Float()

        for (segment in path.segments()) {
            @Suppress("NON_EXHAUSTIVE_WHEN_STATEMENT")
            when (segment.type) {
                PathSegmentType.MOVE -> shape2.moveTo(segment.points[0].x, segment.points[0].y)
                PathSegmentType.LINE -> shape2.lineTo(segment.points[1].x, segment.points[1].y)
                PathSegmentType.QUADRATIC -> shape2.quadTo(
                    segment.points[1].x, segment.points[1].y,
                    segment.points[2].x, segment.points[2].y
                )
                PathSegmentType.CUBIC -> shape2.curveTo(
                    segment.points[1].x, segment.points[1].y,
                    segment.points[2].x, segment.points[2].y,
                    segment.points[3].x, segment.points[3].y
                )
                PathSegmentType.CLOSE -> shape2.closePath()
                PathSegmentType.DONE -> {}
            }
        }

        fun drawShape(shape: Shape): BufferedImage {
            val width = 76
            val height = 76
            return createBufferedImage(width, height).applyGraphics2D {
                background = Color.WHITE
                clearRect(0, 0, width, height)
                paint = Color.BLUE
                stroke = BasicStroke(2.0f)
                setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
                translate(12, 12)
                draw(shape)
            }
        }

        val i1 = drawShape(shape1)
        val i2 = drawShape(shape2)

        compareImages(i1, i2)
    }

    private fun compareImages(i1: BufferedImage, i2: BufferedImage, error: Int = 1) {
        assertEquals(i1.width, i2.width)
        assertEquals(i1.height, i2.height)

        val p1 = (i1.data.dataBuffer as DataBufferInt).data
        val p2 = (i2.data.dataBuffer as DataBufferInt).data

        for (x in 0 until i1.width) {
            for (y in 0 until i2.width) {
                val index = y * i1.width + x

                val c1 = p1[index]
                val c2 = p2[index]

                val redDiff = abs(red(c1) - red(c2))
                assertTrue(redDiff <= error, "Difference (red): $redDiff")
                val greenDiff = abs(green(c1) - green(c2))
                assertTrue(greenDiff <= error, "Difference (green): $greenDiff")
                val blueDiff = abs(blue(c1) - blue(c2))
                assertTrue(blueDiff <= error, "Difference (blue): $blueDiff")
            }
        }
    }

    @Suppress("SameParameterValue")
    private fun createBufferedImage(width: Int, height: Int) =
        BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)

    private fun BufferedImage.applyGraphics2D(block: Graphics2D.() -> Unit) =
        apply { (graphics as Graphics2D).also(block) }

    private fun red(color: Int) = color shr 16 and 0xFF

    private fun green(color: Int) = color shr 8 and 0xFF

    private fun blue(color: Int) = color and 0xFF
}
