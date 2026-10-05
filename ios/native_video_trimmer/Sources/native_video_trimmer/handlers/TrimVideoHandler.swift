import Flutter

class TrimVideoHandler: BaseMethodHandler {
    func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        guard let args = call.arguments as? [String: Any],
              let startTime = args["startTimeMs"] as? Int,
              let endTime = args["endTimeMs"] as? Int else {
            result(FlutterError(code: "INVALID_ARGUMENTS",
                              message: "Missing or invalid parameters. Required: startTimeMs, endTimeMs",
                              details: nil))
            return
        }
        
        let includeAudio = args["includeAudio"] as? Bool ?? true
        let qualityRaw = args["quality"] as? String ?? "original"

        guard let quality = VideoQuality(rawValue: qualityRaw) else {
            result(FlutterError(code: "INVALID_ARGUMENTS",
                              message: "Unknown quality '\(qualityRaw)'. Expected one of: original, hd1080, hd720, passthrough, hevc",
                              details: nil))
            return
        }

        // Validate time range
        guard startTime >= 0 && endTime > startTime else {
            result(FlutterError(code: "INVALID_TIME_RANGE",
                              message: "Invalid time range. endTime must be greater than startTime and startTime must be non-negative",
                              details: nil))
            return
        }
        
        
        VideoManager.shared.trimVideo(
            startTimeMs: Int64(startTime),
            endTimeMs: Int64(endTime),
            includeAudio: includeAudio,
            quality: quality
        ) { trimResult in
            switch trimResult {
            case .success(let path):
                result(path)
            case .failure(let error):
                result(FlutterError(code: "TRIM_ERROR",
                                  message: error.localizedDescription,
                                  details: nil))
            }
        }
    }
}
