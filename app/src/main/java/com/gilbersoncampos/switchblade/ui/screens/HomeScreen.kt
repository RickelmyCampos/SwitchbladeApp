package com.gilbersoncampos.switchblade.ui.screens

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import com.gilbersoncampos.switchblade.Analisys
import com.gilbersoncampos.switchblade.BarCodeITF
import com.gilbersoncampos.switchblade.MainActivity
import com.gilbersoncampos.switchblade.R
import com.gilbersoncampos.switchblade.utils.CameraUtils
import com.gilbersoncampos.switchblade.utils.shaders.applyEdgeDetection
import com.gilbersoncampos.switchblade.utils.shaders.applyThreshold
import com.gilbersoncampos.switchblade.utils.shaders.convertToGrayscale

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.gilbersoncampos.switchblade.utils.shaders.rotateBitmap


@Composable
fun HomeScreen() {
    val showScreenCamera = remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current
    val cameraUtils = CameraUtils(context)
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(), onResult = { isGranted ->
            if (isGranted) {
                Log.i("Permissão", "Permission granted")
            } else {
                val rationaleRequired = ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    Manifest.permission.CAMERA
                )

                if (rationaleRequired) {
                    Log.i("Permissão", "Permission requerida")
                } else {
                    Log.i("Permissão", "Permission requerida habilite nas configurações")
                }


            }
        })

//    LazyVerticalGrid(
//        columns = GridCells.Fixed(2),
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp)
//    ) {
//        items(Samples.menu) {
//            ItemMenuComponent(icon = it.icon, title = it.title, onClick = {
//                if (cameraUtils.hasCameraPermission(context)) {
//                    Log.d("Permissão", "permitido ")
//
//                    val intent = Intent(context, CameraActivity::class.java)
//                    context.startActivity(intent)
//
//                } else {
//                    Log.d("Permissão", "Não permitido ")
//                    requestPermissionLauncher.launch(Manifest.permission.CAMERA)
//                }
//            })
//
//        }
//
//
//    }

    // BarCodeView()
    ImageAnalizys()
}

@Composable
fun BarCodeView() {
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        var text by remember { mutableStateOf("0254") }
        var barCode by remember { mutableStateOf("") }
        val barcodeGenerator = BarCodeITF()
        TextField(value = text, onValueChange = { text = it })
        Button(onClick = { barCode = barcodeGenerator.generateItfBarcode(text) }) {
            Text(text = "BarCode")
        }
        Text(text = barCode)
        barcodeGenerator.generateBitmapByItfBarcode(barCode)?.let {
            Image(bitmap = it.asImageBitmap(), contentDescription = null)
        }
    }
}

@Composable
fun ImageAnalizys() {
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        val context = LocalContext.current
        val bitmap = decodeSampledBitmapFromResource(context, R.drawable.barcode_2, 500, 500)

        var bitmapConverted by remember {
            mutableStateOf<Bitmap?>(null)
        }
        val analyzis = Analisys()
        var loading by remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope()
        Image(
            bitmap =if(bitmap.height>bitmap.width) rotateBitmap(bitmap,90f).asImageBitmap() else bitmap.asImageBitmap(),
            contentDescription = null
        )
        Button(onClick = {
            scope.launch(Dispatchers.IO) {
                loading=true
                bitmapConverted = applyThreshold(applyEdgeDetection(convertToGrayscale(if(bitmap.height>bitmap.width) rotateBitmap(bitmap,90f) else bitmap)))
                loading=false
            }
        }) {
            Text(text = "Converter")
        }
        if (loading) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
        }
        bitmapConverted?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = null
            )
            Button(onClick = {

                   //detectBarcodePattern(bitmap)

            }) {
                Text(text = "Gerarcodigo")
            }
        }

    }
}

@Composable
@Preview(showBackground = true)
fun HomeScreenPreview() {
    HomeScreen()
}

object Samples {
    val menu: List<MenuItem> = listOf(
        MenuItem(title = "Qr Code", icon = R.drawable.baseline_qr_code_scanner_24),
        MenuItem(title = "Bar COde", icon = R.drawable.baseline_qr_code_scanner_24)
    )

    class MenuItem(val title: String, @DrawableRes val icon: Int)
}

fun decodeSampledBitmapFromResource(
    context: Context, resId: Int, reqWidth: Int, reqHeight: Int
): Bitmap {
    // Primeiro, obter as dimensões da imagem sem carregá-la em memória
    val options = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
    }
    BitmapFactory.decodeResource(context.resources, resId, options)

    // Calcular o valor de inSampleSize
    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)

    // Agora carregar a imagem redimensionada
    options.inJustDecodeBounds = false
    return BitmapFactory.decodeResource(context.resources, resId, options)
}

// Função para calcular a escala de redução do Bitmap
fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
    val height = options.outHeight
    val width = options.outWidth
    var inSampleSize = 1

    if (height > reqHeight || width > reqWidth) {
        val halfHeight: Int = height / 2
        val halfWidth: Int = width / 2

        // Calcular o maior inSampleSize que é uma potência de 2 e que ainda mantém a largura e altura maiores que as dimensões requeridas
        while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
            inSampleSize *= 2
        }
    }
    return inSampleSize
}