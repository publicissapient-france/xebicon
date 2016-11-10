//
//  ObstacleClearedViewController.swift
//  TrainSubscription
//
//  Created by Jeromin Lebon on 31/10/2016.
//  Copyright © 2016 Xebia. All rights reserved.
//

import Foundation

class ObstacleClearedViewController: UIViewController, UserInfoConfigurable {
    var userInfo: UserInfo?
    var router: Router<KeynoteState>!
    
    @IBOutlet weak var banner: Banner!
    
    override func viewDidLoad() {
        self.navigationController?.navigationBar.barTintColor = Constant.Color.purpleColor
        self.navigationController?.navigationBar.isTranslucent = false
        guard let destination = userInfo?.destination else {
                return
        }
        banner.update(with: destination)
    }

}
