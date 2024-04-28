package com.jdangar.facedetection

import android.graphics.PointF
import androidx.compose.ui.geometry.Size

/*fun adjustPoint(point: PointF, imageWidth: Int, imageHeight: Int, screenWidth: Int, screenHeight: Int): PointF {
    val x = point.x / imageWidth * screenWidth
    val y = point.y / imageHeight * screenHeight
    return PointF(x, y)
}

fun adjustSize(size: Size, imageWidth: Int, imageHeight: Int, screenWidth: Int, screenHeight: Int): Size {
    val width = size.width / imageWidth * screenWidth
    val height = size.height / imageHeight * screenHeight
    return Size(width, height)
}*/

fun adjustPoint(
    point: PointF,
    imageWidth: Int,
    imageHeight: Int,
    screenWidth: Int,
    screenHeight: Int,
    isFrontCamera: Boolean = true
): PointF {
    val imageAspectRatio = imageWidth.toFloat() / imageHeight
    val screenAspectRatio = screenWidth.toFloat() / screenHeight

    val scaleFactor = if (imageAspectRatio > screenAspectRatio) {
        // Width is the limiting factor.
        screenHeight.toFloat() / imageHeight
    } else {
        // Height is the limiting factor.
        screenWidth.toFloat() / imageWidth
    }

    // Calculate the horizontal offset to center the mesh.
    val offsetX = (screenWidth - (imageWidth * scaleFactor)) / 2

    // Adjust x-coordinate for mirroring if using the front camera.
    val x = if (isFrontCamera) {
        screenWidth - ((point.x * scaleFactor) + offsetX) // Mirror the x-coordinate
    } else {
        (point.x * scaleFactor) + offsetX
    }

    val y = point.y * scaleFactor // No change in the y scaling

    return PointF(x, y)
}

fun adjustSize(
    size: Size,
    imageWidth: Int,
    imageHeight: Int,
    screenWidth: Int,
    screenHeight: Int,
    isFrontCamera: Boolean = true
): Size {
    val imageAspectRatio = imageWidth.toFloat() / imageHeight
    val screenAspectRatio = screenWidth.toFloat() / screenHeight

    val scaleFactor = if (imageAspectRatio > screenAspectRatio) {
        // Width is the limiting factor.
        screenHeight.toFloat() / imageHeight
    } else {
        // Height is the limiting factor.
        screenWidth.toFloat() / imageWidth
    }

    // Adjust x-coordinate for mirroring if using the front camera.
    val width = if (isFrontCamera) {
        size.width * -scaleFactor/// imageWidth * screenWidth
    } else {
        size.width * scaleFactor/// imageWidth * screenWidth
    }

    val height = size.height * scaleFactor/// imageHeight * screenHeight
    return Size(width, height)
}
