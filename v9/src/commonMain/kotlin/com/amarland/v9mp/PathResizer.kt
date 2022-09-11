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

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path

class Slice(val start: Float, val end: Float) {

    val size = end - start

    init {
        require(start <= end) {
            """|Invalid slice, start must be <= end:
               |    start = $start
               |    end   = $end
            """.trimMargin()
        }
    }

    override fun toString(): String = "Slice(start=$start, end=$end)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Slice

        if (start != other.start) return false
        if (end != other.end) return false

        return true
    }

    override fun hashCode(): Int {
        var result = start.hashCode()
        result = 31 * result + end.hashCode()
        return result
    }
}

@Suppress("unused")
fun Slices(left: Int, top: Int, right: Int, bottom: Int) =
    Slices(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())

@Suppress("unused")
fun Slices(slices: Rect) = Slices(slices.left, slices.top, slices.right, slices.bottom)

fun Slices(left: Float, top: Float, right: Float, bottom: Float) =
    Slices(listOf(Slice(left, right)), listOf(Slice(top, bottom)))

class Slices(verticalSlices: List<Slice>, horizontalSlices: List<Slice>) {

    val verticalSlices: Array<Slice>
    private val verticalTotal: Float

    val horizontalSlices: Array<Slice>
    private val horizontalTotal: Float

    init {
        require(verticalSlices.isNotEmpty()) { "At least 1 vertical slice is required" }
        require(horizontalSlices.isNotEmpty()) { "At least 1 horizontal slice is required" }

        // TODO: merge overlapping/connected slices
        this.verticalSlices = verticalSlices
            .filter { it.size > 0 }
            .sortedBy { it.start }
            .toTypedArray()
        this.verticalTotal = this.verticalSlices.sumOf { it.size.toDouble() }.toFloat()

        this.horizontalSlices = horizontalSlices
            .filter { it.size > 0 }
            .sortedBy { it.start }
            .toTypedArray()
        this.horizontalTotal = this.horizontalSlices.sumOf { it.size.toDouble() }.toFloat()
    }

    override fun toString(): String {
        return "Slices(" +
            "verticalSlices=${verticalSlices.contentToString()}, " +
            "horizontalSlices=${horizontalSlices.contentToString()})"
    }
}

@Suppress("unused")
fun Path.slice(slices: Slices) = PathResizer(this, slices)

class PathResizer(path: Path, private val slices: Slices) {

    @Suppress("MemberVisibilityCanBePrivate")
    val bounds: Rect = path.getBounds()

    private val segments: ArrayList<PathSegment>

    // We only need 3 points but it makes an algorithm easier later
    private val points = List(4) { Point() }

    private var stretchableWidth = 0.0f
    private var stretchableHeight = 0.0f

    init {
        val pathSegments = path.segments()

        segments = ArrayList(pathSegments.rawSize())
        // TODO: optimize using a large array and next(FloatArray, Int)
        //       even if we waste 8 floats per segment, we should get better locality
        //       compared to PathSegment instances with arrays of PointF
        for (segment in pathSegments) {
            if (segment.type != PathSegmentType.DONE) segments.add(segment)
        }

        for (slice in slices.verticalSlices) stretchableWidth += slice.size
        for (slice in slices.horizontalSlices) stretchableHeight += slice.size
    }

    @Suppress("unused")
    fun resize(width: Float, height: Float, dstPath: Path = Path()): Path {
        require(width >= bounds.width) {
            """|The destination width must be >= original path width:
               |    destination width = $width
               |    source path width = ${bounds.width}
            """.trimMargin()
        }
        require(height >= bounds.height) {
            """|The destination height must be >= original path height:
               |    destination height = $height
               |    source path height = ${bounds.height}
            """.trimMargin()
        }

        dstPath.reset()

        val stretchX = (width - bounds.width) / stretchableWidth
        val stretchY = (height - bounds.height) / stretchableHeight

        for (i in 0 until segments.size) {
            val segment = segments[i]
            val offsetPositions = points
            when (segment.type) {
                PathSegmentType.MOVE -> {
                    offset(segment.points, 0, 0, offsetPositions, slices, stretchX, stretchY)
                    dstPath.moveTo(offsetPositions[0].x, offsetPositions[0].y)
                }
                PathSegmentType.LINE -> {
                    offset(segment.points, 1, 1, offsetPositions, slices, stretchX, stretchY)
                    dstPath.lineTo(offsetPositions[1].x, offsetPositions[1].y)
                }
                PathSegmentType.QUADRATIC -> {
                    offset(segment.points, 1, 2, offsetPositions, slices, stretchX, stretchY)
                    dstPath.quadraticBezierTo(
                        offsetPositions[1].x, offsetPositions[1].y,
                        offsetPositions[2].x, offsetPositions[2].y
                    )
                }
                PathSegmentType.CUBIC -> {
                    offset(segment.points, 1, 3, offsetPositions, slices, stretchX, stretchY)
                    dstPath.cubicTo(
                        offsetPositions[1].x, offsetPositions[1].y,
                        offsetPositions[2].x, offsetPositions[2].y,
                        offsetPositions[3].x, offsetPositions[3].y
                    )
                }
                PathSegmentType.CLOSE -> dstPath.close()
                PathSegmentType.DONE -> {}
            }
        }

        return dstPath
    }
}

private fun offset(
    positions: List<Point>,
    startPosition: Int,
    endPosition: Int,
    offsetPositions: List<Point>,
    slices: Slices,
    stretchX: Float,
    stretchY: Float
) {
    for (i in startPosition..endPosition) {
        offsetPositions[i].x = positions[i].x
        offsetPositions[i].y = positions[i].y
    }

    // NOTE: We could maybe optimize this a little bit using a precomputed sum table.
    //       We would however only save a multiply and a few adds so probably not worth it?
    var position = positions[endPosition].x
    for (slice in slices.verticalSlices) {
        if (position > slice.start) {
            var offset = slice.size * stretchX
            if (position <= slice.end) {
                offset *= (position - slice.start) / slice.size
            }
            for (i in startPosition..endPosition) {
                offsetPositions[i].x += offset
            }
        }
    }

    position = positions[endPosition].y
    for (slice in slices.horizontalSlices) {
        if (position > slice.start) {
            var offset = slice.size * stretchY
            if (position <= slice.end) {
                offset *= (position - slice.start) / slice.size
            }
            for (i in startPosition..endPosition) {
                offsetPositions[i].y += offset
            }
        }
    }
}
