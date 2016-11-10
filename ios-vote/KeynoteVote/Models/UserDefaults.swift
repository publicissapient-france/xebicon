//
//  UserDefaults.swift
//  TrainSubscription
//
//  Created by Julien on 20/09/2016.
//  Copyright © 2016 Xebia. All rights reserved.
//

import Foundation

public struct UDefaults {
  fileprivate static let firebaseTokenKey = "firebaseToken"
  fileprivate static let userDefaults = UserDefaults.standard
  
  public static var firebaseToken: String? {
    get {
      if let token = userDefaults.string(forKey: firebaseTokenKey) {
        return token
      }
      
      return nil
    }
    set {
      userDefaults.set(newValue, forKey: firebaseTokenKey)
    }
  }
}
