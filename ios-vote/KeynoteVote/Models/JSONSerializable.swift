//
//  JSONSerializable.swift
//  TrainSubscription
//
//  Created by Simone Civetta on 19/08/16.
//  Copyright © 2016 Xebia. All rights reserved.
//

import Foundation

protocol JSONSerializable {
  func JSONDictionary() -> [AnyHashable: Any]
}
