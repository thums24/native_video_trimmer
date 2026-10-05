import AVFoundation

/// Output quality preset, mirroring the Dart `VideoQuality` enum.
/// Raw values must match the Dart enum names sent over the method channel.
enum VideoQuality: String {
    case original
    case hd1080
    case hd720
    case passthrough
    case hevc

    /// The preferred `AVAssetExportSession` preset for this quality.
    /// Note: the preset ladder is fixed by AVFoundation; there is no
    /// 1440p entry, so `hd1080` is the finest available downscale cap.
    var preferredPreset: String {
        switch self {
        case .original:
            return AVAssetExportPresetHighestQuality
        case .hd1080:
            return AVAssetExportPreset1920x1080
        case .hd720:
            return AVAssetExportPreset1280x720
        case .passthrough:
            return AVAssetExportPresetPassthrough
        case .hevc:
            return AVAssetExportPresetHEVCHighestQuality
        }
    }
}
