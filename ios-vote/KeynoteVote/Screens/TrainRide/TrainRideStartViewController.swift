//
//  TrainRideViewController.swift
//  TrainSubscription
//
//  Created by Simone Civetta on 19/08/16.
//  Copyright © 2016 Xebia. All rights reserved.
//

import Foundation
import UIKit

class TrainRideStartViewController : UIViewController, UserInfoConfigurable {
  var userInfo: UserInfo?
    var trainRideStartView: TrainRideStartView{return view as! TrainRideStartView}
  
  override func viewDidLoad() {
    self.navigationController?.navigationBar.barTintColor = Constant.Color.purpleColor
    self.navigationController?.navigationBar.isTranslucent = false
    guard let destination = userInfo?.destination else {
        return
    }
    self.trainRideStartView.configureView(trainDestination: destination)
  }
}

class TrainRideStartView: UIView {
    @IBOutlet weak var banner: Banner!
    @IBOutlet weak var trainImage: UIImageView!
    public var destination: TrainDestination?
    
    func configureView(trainDestination: TrainDestination) {
        self.trainImage.image = trainDestination.details().imageTrain
        self.banner.update(with: trainDestination)
    }
}
