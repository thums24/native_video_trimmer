package com.example.video_trimmer.handlers

import androidx.media3.common.util.UnstableApi
import com.example.video_trimmer.BaseMethodHandler
import com.example.video_trimmer.VideoManager
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@UnstableApi
class GetThumbnailDataHandler : BaseMethodHandler {
    override fun handle(call: MethodCall, result: MethodChannel.Result) {
        val positionMs = call.argument<Number>("positionMs")?.toLong()
        val width = call.argument<Number>("width")?.toInt()
        val height = call.argument<Number>("height")?.toInt()
        val quality = call.argument<Number>("quality")?.toInt() ?: 80
        val videoPath = call.argument<String>("path")

        if (positionMs == null) {
            result.error("INVALID_ARGUMENTS", "Missing positionMs parameter", null)
            return
        }

        // Create a new scope that's tied only to this method call
        val methodScope = CoroutineScope(Dispatchers.Main + Job())

        methodScope.launch {
            try {
                // Encoding touches bitmaps; keep it off the main thread.
                val bytes = withContext(Dispatchers.IO) {
                    VideoManager.getInstance().thumbnailData(
                        positionMs = positionMs,
                        width = width,
                        height = height,
                        quality = quality,
                        videoPath = videoPath
                    )
                }
                result.success(bytes)
            } catch (e: Exception) {
                result.error("THUMBNAIL_ERROR", e.message, null)
            } finally {
                methodScope.cancel() // Always clean up the scope
            }
        }
    }
}
