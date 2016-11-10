//
//  Deserialize.swift
//  TrainSubscription
//
//  Created by Jeromin Lebon on 24/10/2016.
//  Copyright © 2016 Xebia. All rights reserved.
//

import Foundation

protocol JSONDeserialization {
    init?(from data: [AnyHashable: Any])
}
