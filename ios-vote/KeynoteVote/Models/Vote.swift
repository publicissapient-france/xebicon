//
//  Vote.swift
//  TrainSubscription
//
//  Created by Simone Civetta on 19/08/16.
//  Copyright © 2016 Xebia. All rights reserved.
//

import Foundation

public enum TrainDestination: Int {
  case bordeaux = 1
  case lyon = 2
}

struct Vote {
  let train: TrainDestination
  let userId: String
}

extension Vote: JSONSerializable {
  fileprivate struct Keys {
    static let media = "media"
    static let trainId = "trainId"
  }
  
  func JSONDictionary() -> [AnyHashable : Any] {
    return [
      Keys.media: "IOS",
      Keys.trainId: train.rawValue
    ]
  }
}

extension Vote: Postable {
  var URL: Foundation.URL {
    return Endpoint.vote.URL as URL
  }
}
