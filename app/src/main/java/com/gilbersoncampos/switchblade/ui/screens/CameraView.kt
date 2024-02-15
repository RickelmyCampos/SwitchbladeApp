package com.gilbersoncampos.switchblade.ui.screens

import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Rect
import android.graphics.RectF
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.gilbersoncampos.switchblade.utils.CameraUtils
import com.gilbersoncampos.switchblade.utils.EventListener

@Composable
fun CameraView() {
    val context = LocalContext.current

    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }
    val cameraUtils = CameraUtils(context = context)

    val textQrCode = remember {
        mutableStateOf("")
    }
    val rectangle = remember {
        mutableStateOf(Rect())
    }
    val listener = object : EventListener {
        override fun onSuccessEvent(value: CameraUtils.ScanResult) {
            if (value.barcodes.size > 0) {
                textQrCode.value = value.barcodes[0].rawValue.toString()


                rectangle.value = value.barcodes[0].boundingBox!!
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
        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())
        Button(onClick = { /*TODO*/ }) {
            Text(text = "X")

        }
        if (textQrCode.value.isNotEmpty()) {
            Canvas(modifier = Modifier
                .fillMaxSize()
                .background(Color(0x435464c9))) {

                drawRect(
                    color = Color(0x55FFFFFF),
                    topLeft = Offset(rectangle.value.top.toFloat(), rectangle.value.left.toFloat()),
                    size = Size(rectangle.value.width().toFloat(), rectangle.value.height().toFloat()),

                )
            }
            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Bottom) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Gray)
                            .padding(
                                16.dp
                            ),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = textQrCode.value)
                        Button(onClick = {
                            textQrCode.value = ""
                            rectangle.value= Rect()
                        }) {
                            Text(text = "Limpar")

                        }
                    }

                }

            }
        }
    }
}






@Composable
@androidx.compose.ui.tooling.preview.Preview
fun CameraViewPreview() {
    Column (Modifier.height(50.dp).background(Color.White)){
        Text(text = "ALGUMA COISa", color = Color(0x435464c9))
    }

}