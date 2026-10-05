# Native Video Trimmer

[![pub package](https://img.shields.io/pub/v/native_video_trimmer.svg)](https://pub.dev/packages/native_video_trimmer)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

A lightweight Flutter plugin for video manipulation using native code. Trim videos without FFmpeg dependency.

## IMPORTANT 

This library focuses specifically on video trimming functionality.
If you need more advanced video editing features (trim, merge, crop, rotate, etc.), consider using [easy_video_editor](https://pub.dev/packages/easy_video_editor).

### Why choose this plugin?

-  **No FFmpeg Dependency**: Uses platform-native video processing capabilities instead of heavy FFmpeg libraries
-  **Lightweight**: Smaller app size and faster processing compared to FFmpeg-based solutions
-  **Native Performance**: Direct use of Media3 (Android) and AVFoundation (iOS) for optimal performance
-  **Memory Efficient**: Processes videos without loading entire files into memory
-  **Privacy Focused**: All processing happens locally on the device

##  Features

-  **Video Loading**: Load and process video files from any source
-  **Precise Trimming**: Trim videos with millisecond precision
-  **Output Quality**: Five presets (`original`, `hd1080`, `hd720`, `passthrough`, `hevc`) to trade file size against encode time
-  **Thumbnails**: Grab JPEG frames at any position, with optional scaling
-  **Native Implementation**: Clean and efficient platform-specific code

##  Installation

Add this to your package's `pubspec.yaml` file:

```yaml
dependencies:
  native_video_trimmer: ^0.1.0
```

Or install via command line:

```bash
flutter pub add native_video_trimmer
```

##  Usage

### Import

```dart
import 'package:native_video_trimmer/native_video_trimmer.dart';
```

### Initialize

```dart
final videoTrimmer = VideoTrimmer();
```

### Load a Video

```dart
await videoTrimmer.loadVideo('/path/to/video.mp4');
```

### Trim Video

```dart
// Trim the first 5 seconds of the video
final trimmedPath = await videoTrimmer.trimVideo(
  startTimeMs: 0,     // Start time in milliseconds
  endTimeMs: 5000,    // End time in milliseconds (5 seconds)
  includeAudio: true, // Optional, default is true
  quality: VideoQuality.hd1080, // Optional, default is VideoQuality.original
);
```

### Output Quality

`quality` trades file size against encode time and compatibility.
There is no 1440p/1600p step: iOS export presets are a fixed ladder
(720p, 1080p, 4K, …), so `hd1080` is the finest downscale cap available.

| Quality | Behavior | Best for |
|---|---|---|
| `original` (default) | Source resolution, H.264, highest quality | Max fidelity, largest files |
| `hd1080` | Downscales sources taller than 1080p to 1080p | 4K phone footage at sane sizes |
| `hd720` | Downscales sources taller than 720p to 720p | Previews, uploads on slow networks |
| `passthrough` | No re-encode; cuts snap to keyframes | Fastest, lossless, size follows source |
| `hevc` | H.265 encode | Smallest files; slower export, check playback support |

Sources already below a cap are never upscaled, and a low-bitrate source
is never re-encoded at a higher bitrate than it already has. If a device
can't do what was asked (e.g. no HEVC encoder), the plugin falls back to a
supported output instead of failing.

### Get Thumbnail

```dart
// Grab a frame at 1.5s, scaled to 320x180
final thumbnailPath = await videoTrimmer.getThumbnail(
  positionMs: 1500, // Position in milliseconds
  width: 320,       // Optional, omit both to keep source size
  height: 180,      // Optional, omit both to keep source size
  quality: 80,      // Optional JPEG quality 0-100, default is 80
);
```

Thumbnails use the loaded video by default. Pass `path` to thumb a file
without loading it first:

```dart
final thumbnailPath = await videoTrimmer.getThumbnail(
  positionMs: 1500,
  path: '/path/to/another.mp4',
);
```

When generating many thumbnails (e.g. a timeline strip), prefer
`getThumbnailData`, which returns the JPEG bytes directly and skips
file I/O:

```dart
final bytes = await videoTrimmer.getThumbnailData(positionMs: 1500);
```

Local files only — network URLs are not supported.

### Clear Cache

```dart
// Clear the cache
await videoTrimmer.clearCache();
```

## Example

Check the [example](example) folder for a sample app demonstrating the plugin.

##  Platform Support

| Platform | Implementation | Minimum Version | Status |
| -------- | -------------- | --------------- | ------ |
| Android  | Media3         | API 21 (5.0)    | ✅     |
| iOS      | AVFoundation   | iOS 11.0 / 15.6 (SPM) | ✅ |

##  Requirements

### Android

- Minimum SDK: API 21 (Android 5.0)
- Target SDK: API 34
- Kotlin: 1.8.22
- AndroidX
- Media3: 1.4.1

### iOS

- Minimum iOS: 11.0 (CocoaPods) / 15.6 (Swift Package Manager)
- Swift: 5.0 (CocoaPods) / 5.9 (Swift Package Manager)
- Xcode: Latest version

##  Contributing

Contributions are always welcome! Here's how you can help:

1.  Report bugs by opening an issue
2.  Suggest new features or improvements
3.  Improve documentation
4.  Submit pull requests

##  License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

##  Author

[iawtk2302](https://github.com/iawtk2302)
[thums24](https://github.com/thums24)

##  Show Your Support

If you find this plugin helpful, please give it a star on [GitHub](https://github.com/thums24/native_video_trimmer)! It helps others discover the plugin and motivates me to keep improving it.
