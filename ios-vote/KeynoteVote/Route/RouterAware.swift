//
//  Routable.swift
//  TrainSubscription
//
//  Created by pjechris on 16/09/16.
//  Copyright © 2016 Xebia. All rights reserved.
//

import Foundation

protocol RouterAware {
  var router: Router<KeynoteState>! { get set }
}
