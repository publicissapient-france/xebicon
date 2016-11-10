//
//  WelcomeViewController.swift
//  TrainSubscription
//
//  Created by pjechris on 16/09/16.
//  Copyright © 2016 Xebia. All rights reserved.
//

import Foundation

class WelcomeViewController : UIViewController, UserInfoConfigurable {
    
    var userInfo: UserInfo?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.navigationItem.title = NSLocalizedString("XEBICON_DATE", tableName: "Localizable", bundle: Bundle().currentBundle(), value: "", comment: "")
        self.navigationController?.navigationBar.barTintColor = Constant.Color.orangeColor
    }
}
