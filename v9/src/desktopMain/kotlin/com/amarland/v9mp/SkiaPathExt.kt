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

import org.jetbrains.skia.Path
import org.jetbrains.skia.Point as SkiaPoint

// https://github.com/JetBrains/skiko/issues/592
@Suppress("FunctionName")
internal fun Path.Companion._convertConicToQuads(
    p0: SkiaPoint,
    p1: SkiaPoint,
    p2: SkiaPoint,
    w: Float,
    pow2: Int
): Array<Point> {
    val pointCount = (1 + 2 * (1 shl pow2))
    val methodName = "_nConvertConicToQuads"
    val nConvertConicToQuads = (Path::class.java).classLoader
        .loadClass("org.jetbrains.skia.PathKt")
        .declaredMethods.singleOrNull { it.name == methodName }
        ?.takeIf { it.trySetAccessible() }
        ?: throw RuntimeException("'$methodName' cannot be accessed")
    val coordinates = FloatArray(pointCount * 2)
    nConvertConicToQuads(null, p0.x, p0.y, p1.x, p1.y, p2.x, p2.y, w, pow2, coordinates)
    return Array(pointCount) { Point(coordinates[2 * it], coordinates[2 * it + 1]) }
}
