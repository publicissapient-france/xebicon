//
//  ObstacleEncounteredViewController.swift
//  TrainSubscription
//
//  Created by Jeromin Lebon on 11/10/2016.
//  Copyright © 2016 Xebia. All rights reserved.
//

import Foundation

class ObstacleEncounteredViewController: UIViewController, UserInfoConfigurable, RouterAware {
    var userInfo: UserInfo?
    var router: Router<KeynoteState>!
    var obstacleEncounteredView: ObstacleEncounteredView{return view as! ObstacleEncounteredView}
    
    override func viewDidLoad() {
        self.navigationController?.navigationBar.barTintColor = Constant.Color.purpleColor
        self.navigationController?.navigationBar.isTranslucent = false
        guard let obstacle = userInfo?.obstacle,
         let destination = userInfo?.destination else {
            return
        }
        self.obstacleEncounteredView.configure(with: obstacle, and: destination)
    }
}

class ObstacleEncounteredView: UIView {
    @IBOutlet weak var descriptionBlockLabel: UILabel!
    @IBOutlet weak var animalImage: UIImageView!
    @IBOutlet weak var banner: Banner!
    
    func configure(with obstacle: Obstacle, and trainDestination: TrainDestination) {
        self.animalImage.image = obstacle.animalImage
        self.descriptionBlockLabel.text = obstacle.descriptionText
        self.banner.update(with: trainDestination)
    }
}
