//
//  TrainRideEndViewController.swift
//  TrainSubscription
//
//  Created by pjechris on 16/09/16.
//  Copyright © 2016 Xebia. All rights reserved.
//

import Foundation

class TrainRideEndViewController : UIViewController, UserInfoConfigurable {

  var userInfo: UserInfo?
  
    var trainRideEndView: TrainRideEndView{return view as! TrainRideEndView}
    
  override func viewDidLoad() {
    self.navigationController?.navigationBar.barTintColor = Constant.Color.purpleColor
    self.navigationController?.navigationBar.isTranslucent = false
    guard let destination = userInfo?.destination else {
        return
    }
    self.trainRideEndView.configure(with: destination)
  }
}

class TrainRideEndView: UIView {
    @IBOutlet weak var trainStationLabel: UILabel!
    @IBOutlet weak var TrainStationImage: UIImageView!
    @IBOutlet weak var banner: Banner!
    
    func configure(with trainDestination: TrainDestination) {
        self.TrainStationImage.image = trainDestination.details().imageStation
        self.trainStationLabel.text = trainDestination.details().station
        self.banner.update(with: trainDestination)
    }
}
