//
//  ObstacleType.swift
//  TrainSubscription
//
//  Created by Jeromin Lebon on 11/10/2016.
//  Copyright © 2016 Xebia. All rights reserved.
//

import Foundation

public struct Obstacle {
    var type: String?
    var blocked: Bool?
    var descriptionText: String?
    var animalImage: UIImage?

    init(type: String, blocked: Bool) {
        self.blocked = blocked
        self.descriptionText = blocked ? NSLocalizedString("OBSTACLE_BLOCKED_TRUE", tableName: "Localizable", bundle: Bundle().currentBundle(), value: "", comment: "") : NSLocalizedString("OBSTACLE_BLOCKED_FALSE", tableName: "Localizable", bundle: Bundle().currentBundle(), value: "", comment: "")
        self.animalImage = UIImage(named: type, in: Bundle().currentBundle(), compatibleWith: nil)
    }
}
