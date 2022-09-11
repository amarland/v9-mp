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

class PathSegment(
    val type: PathSegmentType,
    val points: List<Point>
) {

    companion object {

        val CLOSE = PathSegment(PathSegmentType.CLOSE, emptyList())
        val DONE = PathSegment(PathSegmentType.DONE, emptyList())
    }
}

enum class PathSegmentType { MOVE, LINE, QUADRATIC, CUBIC, CLOSE, DONE }
