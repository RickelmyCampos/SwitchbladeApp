package com.gilbersoncampos.switchblade.utils.shaders

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix

fun convertToGrayscale(bitmap: Bitmap): Bitmap {
    val width = bitmap.width
    val height = bitmap.height
    val grayscaleBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

    for (y in 0 until height) {
        for (x in 0 until width) {
            val pixel = bitmap.getPixel(x, y)
            val red = Color.red(pixel)
            val green = Color.green(pixel)
            val blue = Color.blue(pixel)

            // Calcula o valor de cinza (media dos canais de cor)
            val gray = (0.299 * red + 0.587 * green + 0.114 * blue).toInt()

            // Define o novo pixel com o valor de cinza
            val newPixel = Color.rgb(gray, gray, gray)
            grayscaleBitmap.setPixel(x, y, newPixel)
        }
    }

    return grayscaleBitmap
}
fun applyEdgeDetection(bitmap: Bitmap): Bitmap {
    val width = bitmap.width
    val height = bitmap.height
    val edgeBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

    val sobelX = arrayOf(
        intArrayOf(-1, 0, 1),
        intArrayOf(-2, 0, 2),
        intArrayOf(-1, 0, 1)
    )

    val sobelY = arrayOf(
        intArrayOf(-1, -2, -1),
        intArrayOf(0, 0, 0),
        intArrayOf(1, 2, 1)
    )

    val pixels = IntArray(width * height)
    bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

    val outputPixels = IntArray(width * height)

    for (y in 1 until height - 1) {
        for (x in 1 until width - 1) {
            var gX = 0
            var gY = 0

            for (ky in 0..2) {
                for (kx in 0..2) {
                    val pixel = pixels[(y + ky - 1) * width + (x + kx - 1)]
                    val gray = Color.red(pixel) // Como já está em escala de cinza, pode pegar o canal R

                    gX += gray * sobelX[ky][kx]
                    gY += gray * sobelY[ky][kx]
                }
            }

            // Magnitude da borda
            val edgeValue = Math.sqrt((gX * gX + gY * gY).toDouble()).toInt().coerceIn(0, 255)

            outputPixels[y * width + x] = Color.rgb(edgeValue, edgeValue, edgeValue)
        }
    }

    edgeBitmap.setPixels(outputPixels, 0, width, 0, 0, width, height)

    return edgeBitmap
}
fun applyThreshold(bitmap: Bitmap, threshold: Int = 128): Bitmap {
    val width = bitmap.width
    val height = bitmap.height
    val binarizedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

    for (y in 0 until height) {
        for (x in 0 until width) {
            val pixel = bitmap.getPixel(x, y)
            val gray = Color.red(pixel) // Como está em tons de cinza, podemos pegar o valor do canal R

            // Aplica o threshold para binarização
            val binarizedColor = if (gray < threshold) Color.BLACK else Color.WHITE
            binarizedBitmap.setPixel(x, y, binarizedColor)
        }
    }

    return binarizedBitmap
}
fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
    // Cria uma nova Matrix para aplicar a rotação
    val matrix = Matrix()
    matrix.postRotate(degrees)

    // Cria um novo Bitmap rotacionado usando a Matrix
    return Bitmap.createBitmap(
        bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
    )
}