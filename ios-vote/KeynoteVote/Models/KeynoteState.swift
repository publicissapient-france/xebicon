//
//  KeynoteState.swift
//  TrainSubscription
//
//  Created by Julien on 20/09/2016.
//  Copyright © 2016 Xebia. All rights reserved.
//

import Foundation

public enum KeynoteState: String {
  case Welcome = "KEYNOTE_START"
  case End = "KEYNOTE_END"
  case TrainVote = "VOTE_TRAIN_START"
  case TrainVoteEnd = "VOTE_TRAIN_END"
  case TrainDeparture = "TRAIN_DEPARTURE_START"
  case TrainDepartureEnd = "TRAIN_DEPARTURE_END"
  case XebiconArrival = "TRAIN_POSITION"
  case HotDeploymentStart = "HOT_DEPLOYMENT_START"
  case HotDeploymentEnd = "HOT_DEPLOYMENT_END"
  case AvailabilityStart = "AVAILABILITY_START"
  case AvailabilityEnd = "AVAILABILITY_END"
  case Obstacle = "OBSTACLE"
  case ObstacleCleared = "OBSTACLE_CLEARED"
}

extension KeynoteState: Route {
  var viewController: UIViewController {
    switch(self) {
    case .Welcome, .End:
      return StoryboardScene.Storyboard.instantiateKeynoteWelcome()
    case .TrainVote:
      return StoryboardScene.Storyboard.instantiateTrainVote()
    case .TrainVoteEnd:
      return StoryboardScene.Storyboard.instantiateTrainVoteComplete()
    case .TrainDeparture:
      return StoryboardScene.Storyboard.instantiateTrainRideStart()
    case .TrainDepartureEnd:
      return StoryboardScene.Storyboard.instantiateTrainRideEnd()
    case .AvailabilityStart:
      return StoryboardScene.Storyboard.instantiatePurchaseStart()
    case .AvailabilityEnd:
      return StoryboardScene.Storyboard.instantiatePurchaseEnd()
    case .HotDeploymentStart:
      return StoryboardScene.Storyboard.instantiateDeploymentStart()
    case .HotDeploymentEnd:
      return StoryboardScene.Storyboard.instantiateDeploymentEnd()
    case .Obstacle:
      return StoryboardScene.Storyboard.instantiateObstacle()
    case .ObstacleCleared:
      return StoryboardScene.Storyboard.instantiateObstacleCleared()
    case .XebiconArrival:
      return StoryboardScene.Storyboard.instantiateKeynoteEnd()
    }
  }
}

extension KeynoteState: JSONDeserialization {
    init?(from data: [AnyHashable : Any]) {
        guard let rawValue = data["state"] as? String else {
            return nil
        }
        
        self.init(rawValue: rawValue)
    }
}

extension KeynoteState: Gettable {
    static var URL: Foundation.URL {
        return Endpoint.state.URL as URL
    }
}
