import 'package:flutter/services.dart';
import 'package:native_video_trimmer/src/video_trimmer.dart';
import 'package:native_video_trimmer/src/video_trimmer_method_channel.dart';
import 'package:flutter_test/flutter_test.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  final MethodChannelVideoTrimmer platform = MethodChannelVideoTrimmer();
  const MethodChannel channel = MethodChannel('native_video_trimmer');

  final List<MethodCall> log = <MethodCall>[];

  setUp(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
        .setMockMethodCallHandler(channel, (MethodCall call) async {
      log.add(call);
      if (call.method == 'trimVideo') {
        return '/tmp/video_trimmer_out.mp4';
      }
      if (call.method == 'getThumbnail') {
        return '/tmp/video_trimmer_thumb.jpg';
      }
      return null;
    });
  });

  tearDown(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
        .setMockMethodCallHandler(channel, null);
    log.clear();
  });

  test('trimVideo defaults quality to original', () async {
    final String? path = await platform.trimVideo(
      startTimeMs: 0,
      endTimeMs: 1000,
    );

    expect(path, '/tmp/video_trimmer_out.mp4');
    expect(log, hasLength(1));
    expect(log.single.method, 'trimVideo');
    expect(log.single.arguments['quality'], 'original');
  });

  test('getThumbnail defaults quality to 80 and forwards size', () async {
    final String? path = await platform.getThumbnail(
      positionMs: 1500,
      width: 320,
      height: 180,
    );

    expect(path, '/tmp/video_trimmer_thumb.jpg');
    expect(log, hasLength(1));
    expect(log.single.method, 'getThumbnail');
    expect(log.single.arguments['positionMs'], 1500);
    expect(log.single.arguments['width'], 320);
    expect(log.single.arguments['height'], 180);
    expect(log.single.arguments['quality'], 80);
  });

  test('trimVideo forwards explicit quality and flags', () async {
    await platform.trimVideo(
      startTimeMs: 1000,
      endTimeMs: 5000,
      includeAudio: false,
      quality: VideoQuality.hd1080,
    );

    expect(log, hasLength(1));
    expect(log.single.arguments['startTimeMs'], 1000);
    expect(log.single.arguments['endTimeMs'], 5000);
    expect(log.single.arguments['includeAudio'], isFalse);
    expect(log.single.arguments['quality'], 'hd1080');
  });
}
