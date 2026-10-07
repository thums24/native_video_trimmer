package com.example.native_video_trimmer.handlers

import com.example.native_video_trimmer.BaseMethodHandler
import android.content.Context
import androidx.media3.common.util.UnstableApi
import com.example.native_video_trimmer.VideoManager
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

@UnstableApi
class ClearTrimVideoCacheHandler(private val context: Context) : BaseMethodHandler {
    override fun handle(call: MethodCall, result: MethodChannel.Result) {
        VideoManager.getInstance().clearCache(context)
        result.success(null)
    }
}
