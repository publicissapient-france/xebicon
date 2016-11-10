// Generated using SwiftGen, by O.Halligon — https://github.com/AliSoftware/SwiftGen

import Foundation
import UIKit

private class BundleLocalizer { }

protocol StoryboardSceneType {
  static var storyboardName: String { get }
}

extension StoryboardSceneType {
  static func storyboard() -> UIStoryboard {
    let bundle: Bundle = Bundle(for: BundleLocalizer.self)
    return UIStoryboard(name: self.storyboardName, bundle: bundle)
  }

  static func initialViewController() -> UIViewController {
    guard let vc = storyboard().instantiateInitialViewController() else {
      fatalError("Failed to instantiate initialViewController for \(self.storyboardName)")
    }
    return vc
  }
}

extension StoryboardSceneType where Self: RawRepresentable, Self.RawValue == String {
  func viewController() -> UIViewController {
    return Self.storyboard().instantiateViewController(withIdentifier: self.rawValue)
  }
  static func viewController(identifier: Self) -> UIViewController {
    return identifier.viewController()
  }
}

protocol StoryboardSegueType: RawRepresentable { }

extension UIViewController {
  func performSegue<S: StoryboardSegueType>(segue: S, sender: AnyObject? = nil) where S.RawValue == String {
    performSegue(withIdentifier: segue.rawValue, sender: sender)
  }
}

// swiftlint:disable file_length
// swiftlint:disable type_body_length

struct StoryboardScene {
  enum Storyboard: String, StoryboardSceneType {
    static let storyboardName = "Storyboard"

    static func initialViewController() -> UINavigationController {
      guard let vc = storyboard().instantiateInitialViewController() as? UINavigationController else {
        fatalError("Failed to instantiate initialViewController for \(self.storyboardName)")
      }
      return vc
    }

    case deploymentEndScene = "DeploymentEnd"
    static func instantiateDeploymentEnd() -> DeployEndViewController {
      guard let vc = StoryboardScene.Storyboard.deploymentEndScene.viewController() as? DeployEndViewController
      else {
        fatalError("ViewController 'DeploymentEnd' is not of the expected class DeployEndViewController.")
      }
      return vc
    }

    case deploymentStartScene = "DeploymentStart"
    static func instantiateDeploymentStart() -> DeployStartViewController {
      guard let vc = StoryboardScene.Storyboard.deploymentStartScene.viewController() as? DeployStartViewController
      else {
        fatalError("ViewController 'DeploymentStart' is not of the expected class DeployStartViewController.")
      }
      return vc
    }

    case keynoteEndScene = "KeynoteEnd"
    static func instantiateKeynoteEnd() -> EndViewController {
      guard let vc = StoryboardScene.Storyboard.keynoteEndScene.viewController() as? EndViewController
      else {
        fatalError("ViewController 'KeynoteEnd' is not of the expected class EndViewController.")
      }
      return vc
    }

    case keynoteWelcomeScene = "KeynoteWelcome"
    static func instantiateKeynoteWelcome() -> WelcomeViewController {
      guard let vc = StoryboardScene.Storyboard.keynoteWelcomeScene.viewController() as? WelcomeViewController
      else {
        fatalError("ViewController 'KeynoteWelcome' is not of the expected class WelcomeViewController.")
      }
      return vc
    }

    case obstacleScene = "Obstacle"
    static func instantiateObstacle() -> ObstacleEncounteredViewController {
      guard let vc = StoryboardScene.Storyboard.obstacleScene.viewController() as? ObstacleEncounteredViewController
      else {
        fatalError("ViewController 'Obstacle' is not of the expected class ObstacleEncounteredViewController.")
      }
      return vc
    }

    case obstacleClearedScene = "ObstacleCleared"
    static func instantiateObstacleCleared() -> UIViewController {
      return StoryboardScene.Storyboard.obstacleClearedScene.viewController()
    }

    case purchaseEndScene = "PurchaseEnd"
    static func instantiatePurchaseEnd() -> ItemPurchaseEndViewController {
      guard let vc = StoryboardScene.Storyboard.purchaseEndScene.viewController() as? ItemPurchaseEndViewController
      else {
        fatalError("ViewController 'PurchaseEnd' is not of the expected class ItemPurchaseEndViewController.")
      }
      return vc
    }

    case purchaseStartScene = "PurchaseStart"
    static func instantiatePurchaseStart() -> ItemPurchaseViewController {
      guard let vc = StoryboardScene.Storyboard.purchaseStartScene.viewController() as? ItemPurchaseViewController
      else {
        fatalError("ViewController 'PurchaseStart' is not of the expected class ItemPurchaseViewController.")
      }
      return vc
    }

    case trainRideEndScene = "TrainRideEnd"
    static func instantiateTrainRideEnd() -> TrainRideEndViewController {
      guard let vc = StoryboardScene.Storyboard.trainRideEndScene.viewController() as? TrainRideEndViewController
      else {
        fatalError("ViewController 'TrainRideEnd' is not of the expected class TrainRideEndViewController.")
      }
      return vc
    }

    case trainRideStartScene = "TrainRideStart"
    static func instantiateTrainRideStart() -> TrainRideStartViewController {
      guard let vc = StoryboardScene.Storyboard.trainRideStartScene.viewController() as? TrainRideStartViewController
      else {
        fatalError("ViewController 'TrainRideStart' is not of the expected class TrainRideStartViewController.")
      }
      return vc
    }

    case trainVoteScene = "TrainVote"
    static func instantiateTrainVote() -> TrainVoteViewController {
      guard let vc = StoryboardScene.Storyboard.trainVoteScene.viewController() as? TrainVoteViewController
      else {
        fatalError("ViewController 'TrainVote' is not of the expected class TrainVoteViewController.")
      }
      return vc
    }

    case trainVoteCompleteScene = "TrainVoteComplete"
    static func instantiateTrainVoteComplete() -> TrainVoteCompleteViewController {
      guard let vc = StoryboardScene.Storyboard.trainVoteCompleteScene.viewController() as? TrainVoteCompleteViewController
      else {
        fatalError("ViewController 'TrainVoteComplete' is not of the expected class TrainVoteCompleteViewController.")
      }
      return vc
    }
  }
}

struct StoryboardSegue {
}
