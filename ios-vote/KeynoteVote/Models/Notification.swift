//
//  Notification.swift
//  TrainSubscription
//
//  Created by pjechris on 16/09/16.
//  Copyright © 2016 Xebia. All rights reserved.
//

import Foundation

// TODO Useful?
struct Notification<T> {
    let type: String
    let payload: T
}