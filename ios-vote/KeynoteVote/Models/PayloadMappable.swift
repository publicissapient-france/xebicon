//
//  PayloadMappable.swift
//  TrainSubscription
//
//  Created by Julien on 20/09/2016.
//  Copyright © 2016 Xebia. All rights reserved.
//

import Foundation

public protocol PayloadMappable {
  mutating func map(_ payload: [AnyHashable: Any]) throws
}
