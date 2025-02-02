# v9-mp

[![Build status](https://github.com/amarland/v9-mp/workflows/Build/badge.svg)](https://github.com/amarland/v9-mp/actions?query=workflow%3ABuild)

v9-mp is a Jetpack Compose Multiplatform (Android + Desktop) library that provides the ability to
dynamically resize [Path](https://developer.android.com/reference/kotlin/androidx/compose/ui/graphics/Path)
objects as you would [9-patch bitmaps](https://developer.android.com/studio/write/draw9patch). In
the example below, the chat bubble is dynamically resized while preserving the corners:

https://user-images.githubusercontent.com/869684/186994957-a25978ce-c980-45bd-9b84-452d6334a07e.mov

Each slice of the original path (indicated by the shaded areas in the example above) defines
a horizontal or vertical region of the path that needs to be stretched. A path can be resized
using multiple slices:

https://user-images.githubusercontent.com/869684/186995070-32021ebd-d085-406f-8905-f035ce4559f5.mov

When multiple slices are used, the amount of stretch applied is proportional to the size of
each slice. This behavior is used in the example above to keep the dark bars centered vertically
and to spaced them equally horizontally, thus properly preserving details inside the path.

v9-mp is compatible with Android API 21+.

## How to use

Slicing a `Path` gives you a `PathResizer`. The easiest way to slice is to use a single vertical
and a single horizontal slice:

```kotlin
val pathResizer = path.slice(Slices(9.0f, 7.0f, 15.0f, 13.0f))
```

This syntax follows the convention of passing the top, left, right, and bottom coordinates:
there's a vertical slice from 9.0 to 15.0 and a horizontal slice from 7.0 to 13.0.

You can also explicitly declare a list of slices:

```kotlin
val pathResizer = path.slice(
    Slices(
        listOf(Slice(9.0f, 10.0f), Slice(14.0f, 15.0f)),
        listOf(Slice(5.0f, 6.0f),  Slice(18.0f, 19.0f))
    )
)
```

Once you have a `PathResizer`, you can call the `resize()` method to create a new path
derived from the original, at the desired size:

```kotlin
val resizedPath = pathResizer.resize(width, height)
```

For performance considerations, you can pass an existing path to `resize()`. This path
will be rewound and returned:

```kotlin
val resizedPath = pathResizer.resize(width, height, destinationPath)
// resizedPath === destinationPath
```

## License

Please see LICENSE.
