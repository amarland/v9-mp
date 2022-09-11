/*
 * Copyright 2022 Anthony Marland
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

import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asSkiaPath
import org.jetbrains.skia.PathVerb
import org.jetbrains.skia.Path as SkiaPath
import org.jetbrains.skia.Point as SkiaPoint

actual fun Path.segments(): PathSegments = SkiaPathSegments(asSkiaPath())

class SkiaPathSegments(private val path: SkiaPath) : PathSegments {

    private val actualIterator = sequence {
        for (segment in path) {
            with(segment!!) {
                when (verb) {
                    PathVerb.MOVE -> yield(
                        PathSegment(PathSegmentType.MOVE, listOf(p0!!.toPoint()))
                    )

                    PathVerb.LINE -> yield(
                        PathSegment(
                            PathSegmentType.LINE,
                            listOf(p0!!.toPoint(), p1!!.toPoint())
                        )
                    )

                    PathVerb.QUAD -> yield(
                        PathSegment(
                            PathSegmentType.QUADRATIC,
                            listOf(p0!!.toPoint(), p1!!.toPoint(), p2!!.toPoint())
                        )
                    )

                    PathVerb.CONIC -> yieldAll(
                        convertConicToQuadPathSegments(p0!!, p1!!, p2!!, conicWeight)
                    )

                    PathVerb.CUBIC -> yield(
                        PathSegment(
                            PathSegmentType.CUBIC,
                            listOf(
                                p0!!.toPoint(), p1!!.toPoint(),
                                p2!!.toPoint(), p3!!.toPoint()
                            )
                        )
                    )

                    PathVerb.CLOSE -> yield(PathSegment.CLOSE)
                    PathVerb.DONE -> yield(PathSegment.DONE)
                }
            }
        }
    }.iterator()

    override fun hasNext() = actualIterator.hasNext()

    override fun next() = actualIterator.next()

    override fun rawSize() = path.verbsCount

    private fun convertConicToQuadPathSegments(
        p0: SkiaPoint,
        p1: SkiaPoint,
        p2: SkiaPoint,
        weight: Float
    ): Sequence<PathSegment> {
        val points = SkiaPath._convertConicToQuads(p0, p1, p2, weight, 1)
        return sequence {
            for (i in 1..points.lastIndex step 2) {
                yield(
                    PathSegment(
                        PathSegmentType.QUADRATIC,
                        listOf(points[i - 1], points[i], points[i + 1])
                    )
                )
            }
        }
    }

    private fun SkiaPoint.toPoint() = Point(x, y)
}
