package com.gilbersoncampos.switchblade

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Shader
import kotlinx.coroutines.runBlocking

class Analisys {
   suspend fun detectEdges(bitmap: Bitmap): Bitmap {
       val width = bitmap.width
       val height = bitmap.height
       // Criar um novo bitmap para armazenar a imagem processada
       //val edgeBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

//        for (x in 1 until width - 1) {
//            for (y in 1 until height - 1) {
//                val pixel = bitmap.getPixel(x, y)
//
//                // Acessar os componentes de cor do pixel
//                val red = Color.red(pixel)
//                val green = Color.green(pixel)
//                val blue = Color.blue(pixel)
//
//                // Converter para tons de cinza
//                val gray = (red + green + blue) / 3
//
//                // Se o pixel for escuro o suficiente, considere-o como uma borda (um processo simples)
//                if (gray < 128) {
//                    edgeBitmap.setPixel(x, y, Color.BLACK)
//                } else {
//                    edgeBitmap.setPixel(x, y, Color.WHITE)
//                }
//            }
//        }

return bitmap
   }
}