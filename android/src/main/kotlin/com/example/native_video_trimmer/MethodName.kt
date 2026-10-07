package com.example.native_video_trimmer

enum class MethodName(val method: String) {
    LOAD_VIDEO("loadVideo"),
    TRIM_VIDEO("trimVideo"),
    GET_THUMBNAIL("getThumbnail"),
    GET_THUMBNAIL_DATA("getThumbnailData"),
    CLEAR_TRIM_VIDEO_CACHE("clearTrimVideoCache");

    companion object {
        fun fromString(method: String): MethodName? {
            return values().find { it.method == method }
        }
    }
}