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

class Point(var x: Float = 0F, var y: Float = 0F) {

    operator fun set(x: Float, y: Float) {
        this.x = x
        this.y = y
    }

    fun equals(x: Float, y: Float) = this.x == x && this.y == y

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as Point
        if (other.x.compareTo(x) != 0) return false
        return other.y.compareTo(y) == 0
    }

    override fun hashCode(): Int {
        var result = if (x != 0F) x.toBits() else 0
        result = 31 * result + if (y != 0F) y.toBits() else 0
        return result
    }

    override fun toString() = "Point($x, $y)"
}
