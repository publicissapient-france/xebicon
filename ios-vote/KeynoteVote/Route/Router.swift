//
//  Router.swift
//  TrainSubscription
//
//  Created by Simone Civetta on 19/08/16.
//  Copyright © 2016 Xebia. All rights reserved.
//

import Foundation
import UIKit

class Router<T: Route> {
  
  let navigationController: UINavigationController
  var didNavigate: (UIViewController) -> Void = { _ in }
  
  init(navigationController: UINavigationController) {
    self.navigationController = navigationController
  }

  func navigate(_ route: T) {
    DispatchQueue.main.async {
      let controller = route.viewController

      self.navigationController.setViewControllers([controller], animated: false)
      self.didNavigate(controller)
    }
  }
}
