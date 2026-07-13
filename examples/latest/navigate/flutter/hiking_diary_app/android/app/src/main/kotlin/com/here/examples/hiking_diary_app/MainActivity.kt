package com.here.sdk.examples.hiking_diary_app

import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import java.io.File

class MainActivity : FlutterActivity() {

    // A method channel, as defined in the GPXManager class.
    companion object {
        private const val METHOD_CHANNEL = "com.example.filepath"
    }

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        // Implement the method channel to retrieve a native file path on Android.
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, METHOD_CHANNEL)
            .setMethodCallHandler { call, result ->
                if (call.method == "getFilePath") {
                    // Get the filename from the arguments.
                    val fileName: String? = call.argument("fileName")

                    // Get the app's internal storage directory using filesDir.
                    val internalStorageDir: File? = filesDir

                    if (internalStorageDir != null && fileName != null) {
                        // Create a new File object with the directory and the filename to retrieve the path.
                        val file = File(internalStorageDir, fileName)

                        // Return the absolute path of the file.
                        result.success(file.absolutePath)
                    } else {
                        result.error("UNAVAILABLE", "Internal storage directory not available or invalid file name", null)
                    }
                } else {
                    result.notImplemented()
                }
            }
    }
}
