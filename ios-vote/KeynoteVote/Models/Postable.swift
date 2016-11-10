//
//  Postable.swift
//  TrainSubscription
//
//  Created by Simone Civetta on 19/08/16.
//  Copyright © 2016 Xebia. All rights reserved.
//

import Foundation

protocol Postable {
  var URL: Foundation.URL { get }
  func post(with userInfo: UserInfo, callback: @escaping PostCallback)
}

private struct Keys {
  static let userId = "userId"
}

extension Postable where Self: JSONSerializable {
  func post(with userInfo: UserInfo, callback: @escaping PostCallback) {
    var payload = self.JSONDictionary()
    payload[Keys.userId] = userInfo.token
    
    PostService.send(self.URL, payload: payload, callback: callback)
  }
}
