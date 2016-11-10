//
//  ItemPurchaseEndViewController.swift
//  TrainSubscription
//
//  Created by Julien on 21/09/2016.
//  Copyright © 2016 Xebia. All rights reserved.
//

import Foundation

class ItemPurchaseEndViewController: UIViewController, UserInfoConfigurable {
  var userInfo: UserInfo?
  
    @IBOutlet weak var banner: Banner!
  
  override func viewDidLoad() {
    self.navigationController?.navigationBar.barTintColor = Constant.Color.purpleColor
    self.navigationController?.navigationBar.isTranslucent = false
    guard let trainDetails = userInfo?.destination else {
        return
    }
    self.banner.update(with: trainDetails)
  }
}
