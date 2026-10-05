import 'dart:async';
import 'dart:typed_data';

import 'video_trimmer_platform_interface.dart';

/// Output quality preset for [VideoTrimmer.trimVideo].
///
/// Each value trades file size against encode time and compatibility.
/// The default, [original], preserves the current behavior.
enum VideoQuality {
  /// Source resolution, H.264, highest quality. Largest files.
  original,

  /// Downscales sources taller than 1080p to 1080p, H.264.
  hd1080,

  /// Downscales sources taller than 720p to 720p, H.264.
  hd720,

  /// Copies streams without re-encoding. Fastest and lossless, but cuts
  /// snap to keyframes and size follows the source bitrate.
  passthrough,

  /// HEVC (H.265) encode. Smallest files, slowest export, and older
  /// players may not support playback.
  hevc,
}

class VideoTrimmer {
  /// Loads a video file from the given [path].
  Future<void> loadVideo(String path) {
    return VideoTrimmerPlatform.instance.loadVideo(path);
  }

  /// Trims the loaded video from [startTime] to [endTime].
  /// Returns the path to the trimmed video file.
  /// Times are in milliseconds.
  /// Set [includeAudio] to false to remove audio from the trimmed video.
  /// [quality] controls the output size/encode tradeoff, see [VideoQuality].
  Future<String?> trimVideo({
    required int startTimeMs,
    required int endTimeMs,
    bool includeAudio = true,
    VideoQuality quality = VideoQuality.original,
  }) {
    return VideoTrimmerPlatform.instance.trimVideo(
      startTimeMs: startTimeMs,
      endTimeMs: endTimeMs,
      includeAudio: includeAudio,
      quality: quality,
    );
  }

  /// Generates a JPEG thumbnail at [positionMs] and returns the path
  /// to the thumbnail file.
  /// Time is in milliseconds. When both [width] and [height] are given the
  /// thumbnail is scaled to that size, otherwise the source frame size is
  /// kept. [quality] is the JPEG quality from 0 to 100.
  /// Uses the loaded video, or [path] to thumb a file without loading it.
  Future<String?> getThumbnail({
    required int positionMs,
    int? width,
    int? height,
    int quality = 80,
    String? path,
  }) {
    return VideoTrimmerPlatform.instance.getThumbnail(
      positionMs: positionMs,
      width: width,
      height: height,
      quality: quality,
      path: path,
    );
  }

  /// Same as [getThumbnail] but returns the JPEG bytes directly instead
  /// of writing a file. Prefer this when generating many thumbnails
  /// (e.g. a timeline strip) to avoid file I/O churn.
  Future<Uint8List?> getThumbnailData({
    required int positionMs,
    int? width,
    int? height,
    int quality = 80,
    String? path,
  }) {
    return VideoTrimmerPlatform.instance.getThumbnailData(
      positionMs: positionMs,
      width: width,
      height: height,
      quality: quality,
      path: path,
    );
  }

  /// Clears any cached files created during video trimming
  Future<void> clearCache() {
    return VideoTrimmerPlatform.instance.clearCache();
  }
}
