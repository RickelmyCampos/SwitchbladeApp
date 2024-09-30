package com.gilbersoncampos.switchblade.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

interface EventListener {
    fun onSuccessEvent(value: CameraUtils.ScanResult)
    fun onFailedEvent(value: String)

}

class CameraUtils(private val context: Context) {
    fun hasCameraPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

fun closeCamera(){
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    cameraProviderFuture.get().unbindAll()
}
    fun startCamera(
        previewView: PreviewView,
        lifecycleOwner: LifecycleOwner,
        listener: EventListener
    ) {
        val TAG = "startCameraFunction"
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
            val imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(Executors.newSingleThreadExecutor(), QrCodeAnalysis(listener))
                }
            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalyzer
                )
//                cameraProvider.bindToLifecycle(
//                    context, cameraSelector, preview)

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(context))
    }
    data class ScanResult(val barcodes: List<Barcode>, val imageWidth: Int, val imageHeight: Int)
    private class QrCodeAnalysis(private val listener: EventListener) : ImageAnalysis.Analyzer {
        val options = BarcodeScannerOptions.Builder()
            .enableAllPotentialBarcodes() // Optional
            .build()
        val scanner = BarcodeScanning.getClient()
        private var isBusy = AtomicBoolean(false)

        @OptIn(ExperimentalGetImage::class)
        override fun analyze(imageProxy: ImageProxy) {

            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val image =
                    InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                scanner.process(image)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            task.result?.let { result ->
                                listener.onSuccessEvent(
                                    ScanResult(
                                        result,
                                        image.width,
                                        image.height
                                    )
                                )
                            }
                        } else {
                            Log.w(
                                "BarcodeAnalyzer",
                                "failed to scan image: ${task.exception?.message}"
                            )
                        }
                        imageProxy.close()
                        isBusy.set(false)
                    }


            }
            imageProxy.close()
        }

        private fun scanBarcodes(image: InputImage) {
            // [START set_detector_options]
            val options = BarcodeScannerOptions.Builder()
                .setBarcodeFormats(

                    Barcode.FORMAT_QR_CODE
                )
                .build()
            // [END set_detector_options]

            // [START get_detector]
            val scanner = BarcodeScanning.getClient()
            // Or, to specify the formats to recognize:
            // val scanner = BarcodeScanning.getClient(options)
            // [END get_detector]

            // [START run_detector]
            val result = scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    // Task completed successfully
                    // [START_EXCLUDE]
                    if (!barcodes.isNullOrEmpty()) {

                        Log.d("BARCODE", barcodes[0].boundingBox.toString())
                    }
                }
                .addOnFailureListener {
                    // Task failed with an exception
                    // ...
                    //Log.d("BARCODE", "${it.message}")
                    listener.onFailedEvent(it.message!!)
                }
        }

    }


}


