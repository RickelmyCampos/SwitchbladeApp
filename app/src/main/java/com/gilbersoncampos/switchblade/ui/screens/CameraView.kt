package com.gilbersoncampos.switchblade.ui.screens

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.graphics.Rect
import android.graphics.RectF
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.gilbersoncampos.switchblade.utils.CameraUtils
import com.gilbersoncampos.switchblade.utils.EventListener
import com.google.mlkit.vision.barcode.common.Barcode

@SuppressLint("ServiceCast")
@Composable
fun CameraView(onFinish: () -> Unit) {
    val context = LocalContext.current

    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }
    val cameraUtils = CameraUtils(context = context)

    val textQrCode = remember {
        mutableStateOf("")
    }
    val rectangle = remember {
        mutableStateOf(RectF())
    }
    val listener = object : EventListener {
        override fun onSuccessEvent(value: CameraUtils.ScanResult) {
            if (value.barcodes.size > 0) {
                textQrCode.value = value.barcodes[0].rawValue.toString()
                rectangle.value = ajustarBoundingBox(value.barcodes[0], 2.5f, 2.5f, 2f, 1.7f)
                cameraUtils.closeCamera()
            }
        }

        override fun onFailedEvent(value: String) {
        }

    }
    cameraUtils.startCamera(
        previewView = previewView,
        lifecycleOwner = lifecycleOwner,
        listener = listener
    )


    Box(modifier = Modifier.fillMaxSize()) {
        if (textQrCode.value.isNotEmpty()) {
            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = textQrCode.value)
                    Row() {
                        Button(onClick = {
                            copyText(textQrCode.value, context)
                        }) {
                            Text(text = "Copiar")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = {
                            //TODO(Inplementar click)
                        }) {
                            Text(text = "Ação")
                        }
                    }
                    Button(onClick = {
                        textQrCode.value = ""
                    }) {
                        Text(text = "Ler novamente")
                    }
                }
            }
        } else {
            AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())
        }
        Button(onClick = onFinish) {
            Text(text = "X")
        }
    }
}

fun copyText(text: String, context: Context) {
    val clip = ClipData.newPlainText("Copied Text", text)
    val clipboard = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(clip)
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S)
        Toast.makeText(context, "Texto Copiado", Toast.LENGTH_SHORT).show()
}

// Função para ajustar a bounding box do QR Code detectado
//fun ajustarBoundingBox(barcode: Barcode, fatorDeCorrecao: Float): Rect {
//    // Obtendo a bounding box original
//    val boundingBoxOriginal = barcode.boundingBox
//    if (boundingBoxOriginal != null) {
//        // Calculando o novo tamanho da bounding box
//        val novaLargura = (boundingBoxOriginal.width() * fatorDeCorrecao).toInt()
//        val novaAltura = (boundingBoxOriginal.height() * fatorDeCorrecao).toInt()
//        // Calculando o novo ponto de origem (top-left corner) para manter a bounding box centralizada
//        val novoX = boundingBoxOriginal.centerX() - novaLargura / 2
//        val novoY = boundingBoxOriginal.centerY() - novaAltura / 2
//        // Criando a nova bounding box ajustada
//        val novaBoundingBox =
//            android.graphics.Rect(novoX, novoY, novoX + novaLargura, novoY + novaAltura)
//
//        return novaBoundingBox
//    }
//    return barcode.boundingBox!!
//}
fun ajustarBoundingBox(
    barcode: Barcode,
    escalaX: Float,
    escalaY: Float,
    offsetX: Float,
    offsetY: Float
): RectF {
    // Obtendo a bounding box original
    val boundingBoxOriginal = barcode.boundingBox
    if (boundingBoxOriginal != null) {
        // Ajustando a largura e a altura com os fatores de escala fornecidos
        val novaLargura = (boundingBoxOriginal.width() * escalaX)
        val novaAltura = (boundingBoxOriginal.height() * escalaY)
        // Ajustando a posição com os deslocamentos fornecidos
        val novoY = boundingBoxOriginal.left * offsetX
        val novoX = boundingBoxOriginal.top * offsetY
        // Criando a nova bounding box ajustada
        val novaBoundingBox = RectF(novoX, novoY, novoX + novaLargura, novoY + novaAltura)
        return novaBoundingBox
    } else {
        // Se não houver bounding box original, retorne uma caixa vazia ou lance uma exceção
        return RectF()
    }
}

fun rectangles(rectangle: Rect) {
    Log.d(
        "RECTANGLE ORIGINAL", "" +
                "TOP :${rectangle.top}\n" +
                "LEFT :${rectangle.left}\n" +
                "BOTTOM :${rectangle.bottom}\n" +
                "RIGHT :${rectangle.right}\n" +
                "WE"
    )
}

fun isLandscape() {

}

@Composable
@Preview
fun CameraViewPreview() {
    CameraView({})
}