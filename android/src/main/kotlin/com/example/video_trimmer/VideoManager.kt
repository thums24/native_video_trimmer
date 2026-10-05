package com.example.video_trimmer

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.media3.effect.Presentation
import androidx.media3.transformer.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import java.lang.ref.WeakReference



@UnstableApi
class VideoManager {
    private var currentVideoPath: String? = null
    private var transformer: Transformer? = null
    private val mediaMetadataRetriever = MediaMetadataRetriever()

    companion object {
        @Volatile
        private var instance: VideoManager? = null

        fun getInstance(): VideoManager {
            return instance ?: synchronized(this) {
                instance ?: VideoManager().also { instance = it }
            }
        }
    }

    fun loadVideo(path: String) {
        if (!File(path).exists()) {
            throw VideoException("Video file not found")
        }
        currentVideoPath = path
        mediaMetadataRetriever.setDataSource(path)
    }

    private fun sourceVideoHeight(): Int? =
        mediaMetadataRetriever
            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
            ?.toIntOrNull()

    private fun sourceBitrateBps(): Int? =
        mediaMetadataRetriever
            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)
            ?.toIntOrNull()

   suspend fun trimVideo(
        context: Context,
        startTimeMs: Long,
        endTimeMs: Long,
        includeAudio: Boolean,
        quality: VideoQuality = VideoQuality.ORIGINAL
    ): String {
        val videoPath = currentVideoPath ?: throw VideoException("No video loaded")
        
        // Create output file on IO thread
        val outputFile = withContext(Dispatchers.IO) {
            val timestamp = System.currentTimeMillis()
            val file = File(context.cacheDir, "video_trimmer_$timestamp.mp4")
            if (file.exists()) {
                file.delete()
            }
            file
        }

        // Switch to Main thread for Transformer operations
        return withContext(Dispatchers.Main) {
            suspendCancellableCoroutine { continuation ->
                val mediaItem = MediaItem.Builder()
                    .setUri(Uri.fromFile(File(videoPath)))
                    .setClippingConfiguration(
                        MediaItem.ClippingConfiguration.Builder()
                            .setStartPositionMs(startTimeMs)
                            .setEndPositionMs(endTimeMs)
                            .build()
                    )
                    .build()

                val editedMediaItemBuilder =
                    EditedMediaItem.Builder(mediaItem)
                        .setRemoveAudio(!includeAudio)

                // Downscale only: never upscale a source that is already
                // below the cap.
                VideoQuality.maxHeight(quality)?.let { maxHeight ->
                    sourceVideoHeight()
                        ?.takeIf { it > maxHeight }
                        ?.let {
                            editedMediaItemBuilder.setEffects(
                                Effects(
                                    emptyList(),
                                    listOf(Presentation.createForHeight(maxHeight))
                                )
                            )
                        }
                }
                val editedMediaItem = editedMediaItemBuilder.build()

                val transformerBuilder = Transformer.Builder(context)

                if (quality == VideoQuality.HEVC) {
                    // Transformer falls back to a supported MIME type
                    // automatically when the device cannot encode HEVC.
                    transformerBuilder.setVideoMimeType(MimeTypes.VIDEO_H265)
                }

                // Cap the bitrate at the preset ceiling without ever raising
                // a low-bitrate source up to the cap.
                VideoQuality.bitrateCapBps(quality)?.let { cap ->
                    val bitrate = sourceBitrateBps()
                        ?.takeIf { it > 0 }
                        ?.let { minOf(it, cap) }
                        ?: cap
                    transformerBuilder.setEncoderFactory(
                        DefaultEncoderFactory.Builder(context)
                            .setRequestedVideoEncoderSettings(
                                VideoEncoderSettings.Builder()
                                    .setBitrate(bitrate)
                                    .build()
                            )
                            .build()
                    )
                }

                transformerBuilder
                    .addListener(
                        object : Transformer.Listener {
                            override fun onCompleted(
                                composition: Composition,
                                exportResult: ExportResult
                            ) {
                                continuation.resume(outputFile.absolutePath)
                            }

                            override fun onError(
                                composition: Composition,
                                exportResult: ExportResult,
                                exportException: ExportException
                            ) {
                                continuation.resumeWithException(VideoException("Failed to trim video", exportException))
                            }
                        }
                    )
                    .experimentalSetTrimOptimizationEnabled(true)

                transformer = transformerBuilder.build()
                transformer?.start(editedMediaItem, outputFile.absolutePath)

                continuation.invokeOnCancellation {
                    transformer?.cancel()
                }
            }
        }
    }

    fun thumbnailData(
        positionMs: Long,
        width: Int? = null,
        height: Int? = null,
        quality: Int,
        videoPath: String? = null
    ): ByteArray {
        // A per-call path thumbs a file without disturbing the loaded video.
        // A short-lived retriever is used so the shared instance is untouched.
        val oneShotRetriever = if (videoPath != null) {
            if (!File(videoPath).exists()) {
                throw VideoException("Video file not found")
            }
            MediaMetadataRetriever().also { it.setDataSource(videoPath) }
        } else {
            if (currentVideoPath == null) {
                throw VideoException("No video loaded")
            }
            null
        }
        val retriever = oneShotRetriever ?: mediaMetadataRetriever
        try {
            val bitmap = retriever.getFrameAtTime(
                positionMs * 1000, // Convert to microseconds
                MediaMetadataRetriever.OPTION_CLOSEST_SYNC
            ) ?: throw VideoException("Failed to generate thumbnail")

            val scaledBitmap = if (width != null && height != null) {
                Bitmap.createScaledBitmap(bitmap, width, height, true)
            } else {
                bitmap
            }

            val out = ByteArrayOutputStream()
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)

            if (scaledBitmap !== bitmap) {
                scaledBitmap.recycle()
            }
            bitmap.recycle()

            return out.toByteArray()
        } finally {
            oneShotRetriever?.release()
        }
    }

    suspend fun generateThumbnail(
        context: Context,
        positionMs: Long,
        width: Int? = null,
        height: Int? = null,
        quality: Int,
        videoPath: String? = null
    ): String = withContext(Dispatchers.IO) {
        val bytes = thumbnailData(positionMs, width, height, quality, videoPath)

        val timestamp = System.currentTimeMillis()
        val outputFile = File(context.cacheDir, "video_trimmer_$timestamp.jpg")

        FileOutputStream(outputFile).use { out ->
            out.write(bytes)
        }

        outputFile.absolutePath
    }

    fun clearCache(context: Context) {
        context.cacheDir.listFiles()?.forEach { file ->
            if (file.name.startsWith("video_trimmer_") && 
                (file.extension == "mp4" || file.extension == "jpg")) {
                file.delete()
            }
        }
    }
    fun release() {
        transformer?.cancel()
        transformer = null
        mediaMetadataRetriever.release()
        currentVideoPath = null
        synchronized(this) {
            instance = null
        }
    }
}

class VideoException : Exception {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
}
