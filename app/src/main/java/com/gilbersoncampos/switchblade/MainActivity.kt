package com.gilbersoncampos.switchblade

import android.app.ActivityManager
import android.content.Context
import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.gilbersoncampos.switchblade.ui.screens.HomeScreen
import com.gilbersoncampos.switchblade.ui.theme.SwitchbladeAppTheme
import com.gilbersoncampos.switchblade.utils.shaders.MyGLRenderer

class MainActivity : ComponentActivity() {
    private lateinit var mGLSurfaceView: GLSurfaceView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        mGLSurfaceView = GLSurfaceView(this)
//        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
//        val configurationInfo = activityManager.deviceConfigurationInfo
//        val supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000
//        if(supportsEs2){
//            mGLSurfaceView.setEGLContextClientVersion(2)
//            val  renderer = MyGLRenderer()
//            mGLSurfaceView.setRenderer(renderer)
//            mGLSurfaceView.renderMode=GLSurfaceView.RENDERMODE_WHEN_DIRTY
//
//        }else{
//            return
//        }
//        setContentView(mGLSurfaceView)
        setContent {
            SwitchbladeAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomeScreen()
                }
            }
        }
    }

}

