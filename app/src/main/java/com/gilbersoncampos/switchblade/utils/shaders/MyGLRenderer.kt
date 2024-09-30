package com.gilbersoncampos.switchblade.utils.shaders

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.os.SystemClock
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyGLRenderer : GLSurfaceView.Renderer {
    private var mTriangle1Vertices: FloatBuffer?=null
    private var mTriangle2Vertices: FloatBuffer?=null
    private var mTriangle3Vertices: FloatBuffer?=null

    private val mBytesPerFloat: Int = 4

    private val mViewMatrix = FloatArray(16)

    private var mMVPMatrixHandle:Int = 0
    private var mPositionHandle:Int = 0
    private var mColorHandle:Int = 0

    private val mProjectionMatrix=FloatArray(16)
    private val mModelMatrix=FloatArray(16)


    private val mMVPMatrix=FloatArray(16)
    private val mStrideBytes=7*mBytesPerFloat
    private val mPositionOffset=0
    private val mPositionDataSize=3
    private val mColorOffset=3
    private val mColorDataSize=4
    init {
        val triangle1VerticesData = floatArrayOf(
            -0.5f, -0.25f, 0.0f,
            1.0f, 0.0f, 0.0f, 1.0f,

            0.5f, -0.25f, 0.0f,
            0.0f, 0.0f, 1.0f, 1.0f,

            0.0f, 0.559016994f, 0.0f,
            0.0f, 1.0f, 0.0f, 1.0f
        )
        val triangle2VerticesData = floatArrayOf(
            -0.5f, -0.25f, 0.0f,
            1.0f, 0.0f, 0.0f, 1.0f,

            0.5f, -0.25f, 0.0f,
            0.0f, 0.0f, 1.0f, 1.0f,

            0.0f, 0.559016994f, 0.0f,
            0.0f, 1.0f, 0.0f, 1.0f
        )
        val triangle3VerticesData = floatArrayOf(
            -0.5f, -0.25f, 0.0f,
            1.0f, 0.0f, 0.0f, 1.0f,

            0.5f, -0.25f, 0.0f,
            0.0f, 0.0f, 1.0f, 1.0f,

            0.0f, 0.559016994f, 0.0f,
            0.0f, 1.0f, 0.0f, 1.0f
        )

        mTriangle1Vertices = ByteBuffer.allocateDirect(triangle1VerticesData.size * mBytesPerFloat)
            .order(ByteOrder.nativeOrder()).asFloatBuffer()
        mTriangle2Vertices = ByteBuffer.allocateDirect(triangle2VerticesData.size * mBytesPerFloat)
            .order(ByteOrder.nativeOrder()).asFloatBuffer()
        mTriangle3Vertices = ByteBuffer.allocateDirect(triangle3VerticesData.size * mBytesPerFloat)
            .order(ByteOrder.nativeOrder()).asFloatBuffer()

        mTriangle1Vertices?.put(triangle1VerticesData)?.position(0)
        mTriangle2Vertices?.put(triangle2VerticesData)?.position(0)
        mTriangle3Vertices?.put(triangle3VerticesData)?.position(0)



    }

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 0.5f)
        val eyeX = 0.0f
        val eyeY = 0.0f
        val eyeZ = 1.5f

        val lookX = 0.0f
        val lookY = 0.0f
        val lookZ = -5.0f

        val upX = 0.0f
        val upY = 1.0f
        val upZ = 0.0f

        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ)

        var vertexShaderHandle = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER)

        if (vertexShaderHandle != 0) {
            GLES20.glShaderSource(vertexShaderHandle, vertexShader)
            GLES20.glCompileShader(vertexShaderHandle)
            val compileStatus = IntArray(1)
            GLES20.glGetShaderiv(vertexShaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0)
            if (compileStatus[0] == 0) {
                val errorMsg = GLES20.glGetShaderInfoLog(vertexShaderHandle)
                GLES20.glDeleteShader(vertexShaderHandle)
                vertexShaderHandle = 0
                //throw RuntimeException("Erro ao compilar shader: $errorMsg")
            }
        }
        if (vertexShaderHandle == 0) {
            throw RuntimeException("Error creating vertex shader.")
        }
        var fragmentShaderHandle = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER)

        if (fragmentShaderHandle != 0) {
            GLES20.glShaderSource(fragmentShaderHandle, fragmentShader)
            GLES20.glCompileShader(fragmentShaderHandle)
            val compileStatus = IntArray(1)
            GLES20.glGetShaderiv(fragmentShaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0)
            if (compileStatus[0] == 0) {
                val errorMsg = GLES20.glGetShaderInfoLog(fragmentShaderHandle)
                GLES20.glDeleteShader(fragmentShaderHandle)
                fragmentShaderHandle = 0

            }
        }
        if (fragmentShaderHandle == 0) {
            throw RuntimeException("Error creating fragment shader.")
        }

        var programHandle = GLES20.glCreateProgram()
        if (programHandle != 0) {
            GLES20.glAttachShader(programHandle, vertexShaderHandle)
            GLES20.glAttachShader(programHandle, fragmentShaderHandle)

            GLES20.glBindAttribLocation(programHandle, 0, "a_Position")
            GLES20.glBindAttribLocation(programHandle, 1, "a_Color")
            GLES20.glLinkProgram(programHandle)

            val linkStatus = IntArray(1)
            GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0)
            if (linkStatus[0] == 0) {
                GLES20.glDeleteProgram(programHandle)
                programHandle = 0
            }
        }
        if (programHandle == 0) {
            throw RuntimeException("Error creating program.")
        }

        mMVPMatrixHandle=GLES20.glGetUniformLocation(programHandle,"u_MVPMatrix")
        mPositionHandle=GLES20.glGetAttribLocation(programHandle,"a_Position")
        mColorHandle=GLES20.glGetAttribLocation(programHandle,"a_Color")

        GLES20.glUseProgram(programHandle)
    }

    override fun onSurfaceChanged(p0: GL10?, p1: Int, p2: Int) {
       GLES20.glViewport(0,0,p1,p2)
        val ratio=p1.toFloat()/p2
        val left=-ratio
        val right=ratio
        val bottom=-1.0f
        val top=1.0f
        val near=1.0f
        val far=10.0f

        Matrix.frustumM(mProjectionMatrix,0,left,right, bottom, top, near, far)
    }

    override fun onDrawFrame(p0: GL10?) {
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT or GLES20.GL_COLOR_BUFFER_BIT)

        val time=SystemClock.uptimeMillis()%10000L
        val angleDegrees=(360.0f/10000.0f)*(time.toInt())
        Matrix.setIdentityM(mModelMatrix,0)
        Matrix.rotateM(mModelMatrix,0,angleDegrees,0.0f,0.0f,1.0f)
        mTriangle1Vertices?.let {

        drawTriangle(it)
        }
    }
    private fun drawTriangle(aTriangleBuffer:FloatBuffer){
        aTriangleBuffer.position(mPositionOffset)
        GLES20.glVertexAttribPointer(mPositionHandle,mPositionDataSize,GLES20.GL_FLOAT,false,mStrideBytes,aTriangleBuffer)
        GLES20.glEnableVertexAttribArray(mPositionHandle)

        aTriangleBuffer.position(mColorOffset)
        GLES20.glVertexAttribPointer(mColorHandle,mColorDataSize,GLES20.GL_FLOAT,false,mStrideBytes,aTriangleBuffer)
        GLES20.glEnableVertexAttribArray(mColorHandle)

        Matrix.multiplyMM(mMVPMatrix,0,mViewMatrix,0,mModelMatrix,0)
        Matrix.multiplyMM(mMVPMatrix,0,mProjectionMatrix,0,mMVPMatrix,0)
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle,1,false,mMVPMatrix,0)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES,0,3)

    }

    companion object {
        val vertexShader = """uniform mat4 u_MVPMatrix;      
attribute vec4 a_Position;     
attribute vec4 a_Color;        
varying vec4 v_Color;          
void main()                    
{                              
   v_Color = a_Color;          
   gl_Position = u_MVPMatrix   
               * a_Position;   
}                              
""";
        val fragmentShader = """precision mediump float;       
varying vec4 v_Color;          
void main()                    
{                              
   gl_FragColor = v_Color;     
}                              
"""
    }
}