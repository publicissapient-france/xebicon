//
//  UserInfoViewController.swift
//  TrainSubscription
//
//  Created by Simone Civetta on 19/08/16.
//  Copyright © 2016 Xebia. All rights reserved.
//

import Foundation

public struct UserInfo {
  var token: String?
  var message: String?
  var destination: TrainDestination?
  var obstacle: Obstacle?
  
  public init() {
  }
  
  public init(token: String) {
    self.token = token
  }
}

protocol UserInfoConfigurable {
  var userInfo: UserInfo? { get set }
}
