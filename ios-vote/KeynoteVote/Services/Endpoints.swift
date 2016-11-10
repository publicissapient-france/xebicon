//
//  Endpoints.swift
//  TrainSubscription
//
//  Created by Simone Civetta on 19/08/16.
//  Copyright © 2016 Xebia. All rights reserved.
//

import Foundation

private let baseURL = "http://52.29.163.19:8087"

enum Endpoint: String {
  case vote = "/vote/station"
  case purchase = "/purchase"
  case register = "/register"
  case state = "/state"
  
  var URL: NSURL {
    return NSURL(string: "\(baseURL)\(self.rawValue)")!
  }
}
