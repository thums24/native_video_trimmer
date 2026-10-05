import Flutter
import UIKit

class GetThumbnailHandler: BaseMethodHandler {
    func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        guard let args = call.arguments as? [String: Any],
              let position = args["positionMs"] as? Int else {
            result(FlutterError(code: "INVALID_ARGUMENTS",
                              message: "Missing or invalid positionMs parameter",
                              details: nil))
            return
        }

        let quality = args["quality"] as? Int ?? 80
        var size: CGSize?
        if let width = args["width"] as? Int,
           let height = args["height"] as? Int {
            size = CGSize(width: width, height: height)
        }

        do {
            let path = try VideoManager.shared.generateThumbnail(
                atMs: Int64(position),
                size: size,
                quality: quality
            )
            result(path)
        } catch {
            result(FlutterError(code: "THUMBNAIL_ERROR",
                              message: error.localizedDescription,
                              details: nil))
        }
    }
}
