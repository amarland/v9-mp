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
import androidx.compose.ui.graphics.asAndroidPath
import dev.romainguy.graphics.path.iterator
import android.graphics.Path as AndroidPath
import dev.romainguy.graphics.path.PathSegment as PathwayPathSegment

actual fun Path.segments(): PathSegments = AndroidPathSegments(asAndroidPath())

class AndroidPathSegments(path: AndroidPath) : PathSegments {

    private val actualIterator = path.iterator()

    override fun hasNext() = actualIterator.hasNext()

    override fun next() = with(actualIterator.next()) {
        PathSegment(
            type = when (type) {
                PathwayPathSegment.Type.Move -> PathSegmentType.MOVE
                PathwayPathSegment.Type.Line -> PathSegmentType.LINE
                PathwayPathSegment.Type.Quadratic -> PathSegmentType.QUADRATIC
                PathwayPathSegment.Type.Conic -> throw RuntimeException("Unexpected conic!")
                PathwayPathSegment.Type.Cubic -> PathSegmentType.CUBIC
                PathwayPathSegment.Type.Close -> PathSegmentType.CLOSE
                PathwayPathSegment.Type.Done -> PathSegmentType.DONE
            },
            points = points.map { Point(it.x, it.y) }
        )
    }

    override fun rawSize() = actualIterator.rawSize()
}
