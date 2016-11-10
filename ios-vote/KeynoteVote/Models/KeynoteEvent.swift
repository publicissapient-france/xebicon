//
//  KeynoteState.swift
//  TrainSubscription
//
//  Created by pjechris on 16/09/16.
//  Copyright © 2016 Xebia. All rights reserved.
//

import Foundation

public struct KeynoteEvent {
  public var state: KeynoteState?
  public var message: String?
  public var destination: TrainDestination? = nil
  public var obstacle: Obstacle?
    
  public init() {}
}

private enum PayloadMappingError: Error {
  case noStateFound
  case noObstacleFound
}

extension KeynoteEvent: PayloadMappable {
  mutating public func map(_ payload: [AnyHashable : Any]) throws {

    guard let stateRaw = payload["keynoteState"] as? String, let state = KeynoteState(rawValue: stateRaw) else {
      throw PayloadMappingError.noStateFound
    }

    self.state = state
    self.message = payload["message"] as? String
    self.destination = payload["trainId"].flatMap { TrainDestination(rawValue: Int($0 as! String)!) }

    if state == .Obstacle {
        guard let blocked = payload["blocked"] as? String, let obstacleType = payload["obstacleType"] as? String else {
            throw PayloadMappingError.noObstacleFound
        }

        self.obstacle = Obstacle(type: obstacleType.lowercased(), blocked: NSString(string: blocked).boolValue)
    }
  }
}




