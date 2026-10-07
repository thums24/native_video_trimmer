package com.example.native_video_trimmer

/**
 * Output quality preset, mirroring the Dart `VideoQuality` enum.
 * Names are matched case-insensitively against the Dart enum names
 * sent over the method channel.
 */
enum class VideoQuality {
    ORIGINAL,
    HD1080,
    HD720,
    PASSTHROUGH,
    HEVC;

    companion object {
        /** Cap applied to the frame height for resolution-capped presets. */
        fun maxHeight(quality: VideoQuality): Int? = when (quality) {
            HD1080 -> 1080
            HD720 -> 720
            else -> null
        }

        /** Video bitrate cap in bits per second, or null to leave it unset. */
        fun bitrateCapBps(quality: VideoQuality): Int? = when (quality) {
            HD1080 -> 8_000_000
            HD720 -> 5_000_000
            HEVC -> 8_000_000
            else -> null
        }

        fun parse(raw: String?): VideoQuality {
            if (raw == null) return ORIGINAL
            return try {
                valueOf(raw.uppercase())
            } catch (e: IllegalArgumentException) {
                throw VideoException(
                    "Unknown quality '$raw'. " +
                        "Expected one of: original, hd1080, hd720, passthrough, hevc"
                )
            }
        }
    }
}
