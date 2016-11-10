//
//  DeployViewController.swift
//  TrainSubscription
//
//  Created by pjechris on 16/09/16.
//  Copyright © 2016 Xebia. All rights reserved.
//

import Foundation

class DeployStartViewController : UIViewController, UserInfoConfigurable {

  var userInfo: UserInfo?
    @IBOutlet weak var banner: Banner!
  
  override func viewDidLoad() {
    self.navigationController?.navigationBar.barTintColor = Constant.Color.purpleColor
    self.navigationController?.navigationBar.isTranslucent = false
    guard let destination = userInfo?.destination else {
        return
    }
    self.banner.update(with: destination)
  }
}
