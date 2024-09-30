package com.gilbersoncampos.switchblade

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.Log

class BarCodeITF {
    fun generateBarCodeByItf(itfBarcode: String): String {
        val barCode = itfBarcode.chunked(5).fold("") { acc, item ->
            val key: String =
                Constants.itfTable.filterValues { it == item }.keys.firstOrNull()?.toString() ?: ""
            key + acc
        }
        return barCode
    }

    fun generateItfBarcode(code: String): String {
        try {
            code.toInt()
            if (code.count() % 2 != 0) throw Exception("Não é par")
            var itfBarcode = ""
            code.forEach {
                itfBarcode += Constants.itfTable[it.digitToInt()]
            }
            return itfBarcode
        } catch (e: Exception) {
            return e.message.toString()
        }
    }

    fun generateBitmapByItfBarcode(itfBarcode: String): Bitmap? {
        try {
            val step = 10
            val width = calculateWidth(itfBarcode + Constants.delimitersITF, step)
            val height = 200
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

            val canvas = Canvas(bitmap)

            // Preencher o fundo com uma cor (branco)
            canvas.drawColor(Color.WHITE)

            // Configurar o Paint para desenhar
            val paint = Paint().apply {
                color = Color.BLACK
                style = Paint.Style.FILL
            }
            val paintW = Paint().apply {
                color = Color.RED
                style = Paint.Style.FILL
            }
            val itfBarcodeChunked = itfBarcode.chunked(10)
            val initial = 0
            var next = initial
            next = startITF(next, step, canvas, paint, height)
            next = drawCodeItf(itfBarcodeChunked, next, step, canvas, paint, height)
            // Desenhar um círculo vermelho no centro do Bitmap
            endITF(next, step, canvas, paint, height)
            return bitmap
        } catch (ex: Exception) {
            Log.e("ERROR", ex.message.toString())
            return null
        }
    }

    private fun calculateWidth(itfBarcode: String, step: Int): Int {
        val width =
            itfBarcode.fold(0) { acc, item -> if (item == '1') acc + step * 2 else acc + step }
        return width
    }

    private fun drawCodeItf(
        itfBarcodeChunked: List<String>,
        next: Int,
        step: Int,
        canvas: Canvas,
        paint: Paint,
        height: Int
    ): Int {
        var next1 = next
        itfBarcodeChunked.forEachIndexed { index, itfPair ->
            repeat(5) { position ->
                var multiplyFirst = 1
                var multiplySecond = 1
                val pair = itfPair.chunked(5)
                if (pair[0][position] == '1') multiplyFirst = 2
                var prox = next1 + step * multiplyFirst
                val rectBlack = Rect(next1, 0, prox, height)
                canvas.drawRect(rectBlack, paint)
                if (pair[1][position] == '1') multiplySecond = 2
                prox += step * multiplySecond
                next1 = prox
            }
            //drawPairBarcode(itfPair.chunked(2), (index*10), step, canvas, paint)
        }
        return next1
    }

    private fun endITF(
        next: Int,
        step: Int,
        canvas: Canvas,
        paint: Paint,
        height: Int
    ) {
        var next1 = next
        var multiply = 2
        repeat(2) {
            var prox = next1 + step * multiply
            val rectBlack = Rect(next1, 0, prox, height)
            canvas.drawRect(rectBlack, paint)
            prox += step
            next1 = prox
            multiply = 1
        }
    }

    private fun startITF(
        next: Int,
        step: Int,
        canvas: Canvas,
        paint: Paint,
        height: Int
    ): Int {
        var next1 = next
        repeat(2) {
            var prox = next1 + step * 1
            val rectBlack = Rect(next1, 0, prox, height)
            canvas.drawRect(rectBlack, paint)
            prox += step
            next1 = prox
        }
        return next1
    }
}