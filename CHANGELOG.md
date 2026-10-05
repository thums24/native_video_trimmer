## 0.1.0

- Initial public release (the earlier 1.x line was retracted before adoption).
- Trim output `quality` presets, thumbnail generation (`getThumbnail`, `getThumbnailData`, per-call `path`), iOS Swift Package Manager support.
- Native plugin classes named `NativeVideoTrimmerPlugin` to coexist with `flutter_native_video_trimmer` without a registrar collision.

## 1.1.1

- Rename native plugin classes to `NativeVideoTrimmerPlugin` to avoid a launch crash when coexisting with `flutter_native_video_trimmer` (duplicate registrar key)

## 1.1.0

- Add `getThumbnailData` returning JPEG bytes without file I/O
- `getThumbnail` accepts an optional `path` to thumb a file without loading it

## 1.0.1

- Updated README documentation

## 1.0.0

- Initial release of `native_video_trimmer`, renamed from `flutter_native_video_trimmer` (last upstream release 1.1.9)
- Trim output `quality` presets: `original`, `hd1080`, `hd720`, `passthrough`, `hevc`
- Thumbnail generation via `getThumbnail`
- iOS Swift Package Manager support
- Fix iOS `clearCache` missing trimmed videos; validate trim time range on Android

## 1.1.9 and earlier (as `flutter_native_video_trimmer`)

- Update README.md

## 1.1.8

- Update minimum iOS version to 11.0

## 1.1.7

- Remove thumbnail generation feature
- Update README.md

## 1.1.6

- Fix bug in trimVideo method

## 1.1.5

- Add option includeAudio to trimVideo method

## 1.1.4

- Refactor code

## 1.1.3

- Optimize code
- Update readme

## 1.1.2

- Fix bug on Android build (#1)
- Update readme

## 1.1.1

- Fix bug in trimVideo method

## 1.1.0

- Fix bug in trimVideo method

## 1.0.9

- Refactor code

## 1.0.8

- Update readme

## 1.0.7

- Update clearCache method and parameter type to return MediaInfo

## 1.0.6

- Fix iOS implementation

## 1.0.5

- Add MediaInfo model class for better type safety
- Update getVideoInfo to return MediaInfo instead of Map

## 1.0.4

- Update method channel name to match package name

## 1.0.3

- Fix iOS implementation
- Add Flutter import to iOS handler files
- Fix method channel implementation

## 1.0.2

- Fix iOS implementation
- Fix method channel implementation
- Update package export configuration

## 1.0.1

- Fix package export configuration
- Fix iOS podspec configuration

## 1.0.0

- Initial release
- Features:
  - Video trimming using native code
  - Thumbnail generation
  - Video information retrieval
  - No FFmpeg dependency
  - Support for Android and iOS
- Documentation:
  - Comprehensive README with usage examples
  - API documentation
  - Example app
