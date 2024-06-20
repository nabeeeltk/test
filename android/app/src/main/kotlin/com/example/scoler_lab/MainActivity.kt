package com.example.scoler_lab

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.play.core.splitinstall.*
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugins.GeneratedPluginRegistrant
import io.flutter.plugin.common.MethodChannel

class MainActivity : FlutterActivity() {
    private val DCHANNEL = "com.scholarlab/download"
    private val OCHANNEL = "com.scholarlab/open"

    private var mySessionId = 0
    private val TAG = "MainActivity"

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        GeneratedPluginRegistrant.registerWith(flutterEngine)
        MethodChannel(
            flutterEngine.dartExecutor.binaryMessenger,
            DCHANNEL
        ).setMethodCallHandler { call, result ->
            if (call.method == "DownloadModule") {
                downloadDynamicModule(result)
            } else {
                result.notImplemented()
            }
        }
        MethodChannel(
            flutterEngine.dartExecutor.binaryMessenger,
            OCHANNEL
        ).setMethodCallHandler { call, result ->
            if (call.method == "StartSecondActivity") {
                openDynamicModule(result)
            } else {
                result.notImplemented()
            }
        }
    }

    private fun downloadDynamicModule(result: MethodChannel.Result) {
        val splitInstallManager: SplitInstallManager = SplitInstallManagerFactory.create(this)
        val request: SplitInstallRequest = SplitInstallRequest
            .newBuilder()
            .addModule("unityLibrary")
            .build()
        val listener: SplitInstallStateUpdatedListener =
            SplitInstallStateUpdatedListener { splitInstallSessionState ->
                if (splitInstallSessionState.sessionId == mySessionId) {
                    when (splitInstallSessionState.status()) {
                        SplitInstallSessionStatus.INSTALLED -> {
                            Log.d(TAG, "Dynamic Module downloaded")
                            Toast.makeText(
                                this@MainActivity,
                                "Dynamic Module downloaded",
                                Toast.LENGTH_SHORT
                            ).show()
                            result.success("Dynamic Module downloaded")
                        }
                        SplitInstallSessionStatus.FAILED -> {
                            Log.d(TAG, "Dynamic Module download failed")
                            result.error("ERROR", "Dynamic Module download failed", null)
                        }
                        else -> {
                            Log.d(TAG, "Dynamic Module download status: ${splitInstallSessionState.status()}")
                        }
                    }
                }
            }
        splitInstallManager.registerListener(listener)
        splitInstallManager.startInstall(request)
            .addOnFailureListener { e -> Log.d(TAG, "Exception: $e"); result.error("ERROR", e.message, null) }
            .addOnSuccessListener { sessionId ->
                if (sessionId != null) {
                    mySessionId = sessionId
                }
            }
    }

    private fun openDynamicModule(result: MethodChannel.Result) {
        SplitInstallHelper.loadLibrary(applicationContext, "il2cpp")
        System.loadLibrary("unity")

        val intent = Intent()
        intent.setClassName("com.escavel.integration", "com.unity3d.player.UnityPlayerActivity")
            .putExtra(
                "paramValue",
                "https://link,client-token,unique-email-id"
            )
        startActivity(intent)
        result.success("Opened Unity Activity")
    }
}
