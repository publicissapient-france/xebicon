//
//  TrainVoteViewController.swift
//  TrainSubscription
//
//  Created by Simone Civetta on 19/08/16.
//  Copyright © 2016 Xebia. All rights reserved.
//

import Foundation
import UIKit

class TrainVoteViewController : UIViewController, UserInfoConfigurable, RouterAware, TrainVoteViewDelegate {
  
  var userInfo: UserInfo?
  var router: Router<KeynoteState>!
  var trainVoteView: TrainVoteView { return view as! TrainVoteView }
    var vote: Int?
    
  override func viewDidLoad() {
    trainVoteView.delegate = self
    self.navigationController?.navigationBar.barTintColor = Constant.Color.purpleColor
    self.navigationController?.navigationBar.isTranslucent = false
  }
  
  func voteForTrain(_ trainId: NSInteger) {
    guard let userInfo = userInfo else {
      return
    }
    
    guard let train = TrainDestination(rawValue: trainId) else {
      return
    }
    
    if let token = userInfo.token {
      let vote = Vote(train: train, userId: token)
      
      vote.post(with: userInfo) { (data, error) in
        if let error = error {
          print(error)
          
          return
        }
        print(data)
      }
    }
  }
    
    func didVote(_ vote: Int) {
        self.vote = vote
        voteForTrain(vote)
    }

    func canVote() -> Bool {
        return self.vote == nil
    }
}
