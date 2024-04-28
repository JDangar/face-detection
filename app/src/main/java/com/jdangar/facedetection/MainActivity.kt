@file:OptIn(ExperimentalPermissionsApi::class)

package com.jdangar.facedetection

import android.Manifest
import android.graphics.PointF
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toComposeRect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionRequired
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.face.Face
import com.jdangar.facedetection.ui.theme.FaceDetectionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FaceDetectionTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val context = LocalContext.current
                    val cameraPermissionState =
                        rememberPermissionState(permission = Manifest.permission.CAMERA)

                    PermissionRequired(
                        permissionState = cameraPermissionState,
                        permissionNotGrantedContent = {
                            LaunchedEffect(Unit) {
                                cameraPermissionState.launchPermissionRequest()
                            }
                        },
                        permissionNotAvailableContent = {
                            Toast.makeText(context, "Permission denied.", Toast.LENGTH_LONG).show()
                        },
                        content = {
                            FaceDetectionScreen()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun FaceDetectionScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        val context = LocalContext.current
        val lifecycleOwner = LocalLifecycleOwner.current
        val detectedFaces = remember { mutableStateListOf<Face>() }

        val screenWidth = remember { mutableStateOf(context.resources.displayMetrics.widthPixels) }
        val screenHeight = remember { mutableStateOf(context.resources.displayMetrics.heightPixels) }

        val imageWidth = remember { mutableStateOf(0) }
        val imageHeight = remember { mutableStateOf(0) }

        Box(modifier = Modifier.fillMaxSize()) {
            CameraView(
                context = context,
                lifecycleOwner = lifecycleOwner,
                analyzer = FaceDetectionAnalyzer { faces, width, height ->
                    detectedFaces.clear()
                    detectedFaces.addAll(faces)
                    imageWidth.value = width
                    imageHeight.value = height
                }
            )

            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxHeight()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Face Detection",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                }
            }

            DrawFaces(
                faces = detectedFaces,
                imageHeight.value,
                imageWidth.value,
                screenWidth.value,
                screenHeight.value
            )
        }
    }
}

@Composable
private fun DrawFaces(
    faces: List<Face>,
    imageWidth: Int,
    imageHeight: Int,
    screenWidth: Int,
    screenHeight: Int
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        faces.forEach { face ->
            val boundingBox = face.boundingBox.toComposeRect()
            val topLeft = adjustPoint(
                PointF(boundingBox.topLeft.x, boundingBox.topLeft.y),
                imageWidth,
                imageHeight,
                screenWidth,
                screenHeight
            )
            val size =
                adjustSize(boundingBox.size, imageWidth, imageHeight, screenWidth, screenHeight)

            drawRect(
                color = Color.Yellow,
                size = size,
                topLeft = Offset(topLeft.x, topLeft.y),
                style = Stroke(width = 5f)
            )

        }
    }
}