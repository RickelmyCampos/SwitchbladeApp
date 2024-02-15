package com.gilbersoncampos.switchblade.ui.screens

import android.Manifest
import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.gilbersoncampos.switchblade.CameraActivity
import com.gilbersoncampos.switchblade.MainActivity
import com.gilbersoncampos.switchblade.R
import com.gilbersoncampos.switchblade.ui.components.ItemMenuComponent
import com.gilbersoncampos.switchblade.utils.CameraUtils

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

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(Samples.menu) {
                ItemMenuComponent(icon = it.icon, title = it.title, onClick = {
                    if (cameraUtils.hasCameraPermission(context)) {
                        Log.d("Permissão","permitido ")

                        val intent = Intent(context, CameraActivity::class.java)
                        context.startActivity(intent)

                    } else {
                        Log.d("Permissão","Não permitido ")
                        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                })

            }
        }



}

@Composable
@Preview
fun HomeScreenPreview() {
    HomeScreen()
}

object Samples {
    val menu: List<MenuItem> = listOf(
        MenuItem(title = "Qr Code", icon = R.drawable.baseline_qr_code_scanner_24)
    )

    class MenuItem(val title: String, @DrawableRes val icon: Int)
}