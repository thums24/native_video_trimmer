import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'video_trimmer.dart';
import 'video_trimmer_platform_interface.dart';

/// An implementation of [VideoTrimmerPlatform] that uses method channels.
class MethodChannelVideoTrimmer extends VideoTrimmerPlatform {
  @visibleForTesting
  final methodChannel = const MethodChannel('native_video_trimmer');

  @override
  Future<void> loadVideo(String path) async {
    await methodChannel.invokeMethod<void>('loadVideo', {'path': path});
  }

  @override
  Future<String?> trimVideo({
    required int startTimeMs,
    required int endTimeMs,
    bool includeAudio = true,
    VideoQuality quality = VideoQuality.original,
  }) async {
    final result = await methodChannel.invokeMethod<String>('trimVideo', {
      'startTimeMs': startTimeMs,
      'endTimeMs': endTimeMs,
      'includeAudio': includeAudio,
      'quality': quality.name,
    });
    return result;
  }

  @override
  Future<String?> getThumbnail({
    required int positionMs,
    int? width,
    int? height,
    int quality = 80,
    String? path,
  }) async {
    final result = await methodChannel.invokeMethod<String>('getThumbnail', {
      'positionMs': positionMs,
      'width': width,
      'height': height,
      'quality': quality,
      'path': path,
    });
    return result;
  }

  @override
  Future<Uint8List?> getThumbnailData({
    required int positionMs,
    int? width,
    int? height,
    int quality = 80,
    String? path,
  }) async {
    final result =
        await methodChannel.invokeMethod<Uint8List>('getThumbnailData', {
      'positionMs': positionMs,
      'width': width,
      'height': height,
      'quality': quality,
      'path': path,
    });
    return result;
  }

  @override
  Future<void> clearCache() async {
    await methodChannel.invokeMethod<void>('clearTrimVideoCache');
  }
}
