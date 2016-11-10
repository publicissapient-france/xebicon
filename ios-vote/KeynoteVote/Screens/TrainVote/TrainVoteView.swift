//
//  TrainVoteView.swift
//  TrainSubscription
//
//  Created by Jeromin Lebon on 04/10/2016.
//  Copyright © 2016 Xebia. All rights reserved.
//

import Foundation

protocol TrainVoteViewDelegate: class {
    func didVote(_ vote: Int)
    func canVote() -> Bool
}

class TrainVoteView: UIView {
    @IBOutlet weak var voteDescriptionLabel: UILabel!
    @IBOutlet weak var lynView: UIView!
    @IBOutlet weak var bdxView: UIView!
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var versusImage: UIImageView!
    @IBOutlet weak var voteButton: UIButton!
    
    weak var delegate: TrainVoteViewDelegate?
    var voteNb = 0
    
    override func awakeFromNib() {
        customizeVoteButton()
    }
    
    @IBAction func vote() {
        if (self.voteNb > 0){
            delegate?.didVote(self.voteNb)
            voteDelivered(self.voteNb)
        }
    }
    
    @IBAction func bdxVoteSelectionned() {
        guard self.delegate?.canVote() ?? false else {
            return
        }

        UIView.animate(withDuration: 0.5) {
            self.bdxView.alpha = 1.0
            self.lynView.alpha = 0.4
            self.voteButton.backgroundColor = Constant.Color.purpleColor
        }
        self.voteButton.isHidden = false
        self.voteNb = TrainDestination.bordeaux.rawValue
    }
    
    @IBAction func lyonVoteSelectionned() {
        guard self.delegate?.canVote() ?? false else {
            return
        }

        UIView.animate(withDuration: 0.5) {
            self.lynView.alpha = 1.0
            self.bdxView.alpha = 0.4
            self.voteButton.backgroundColor = Constant.Color.orangeColor
        }
        self.voteButton.isHidden = false
        self.voteNb = TrainDestination.lyon.rawValue
    }
    
    func voteDelivered(_ selection: Int) {
        titleLabel.text = "Vous avez selectionné"
        switch selection {
        case TrainDestination.bordeaux.rawValue:
            finishAnimation(bdxView, withViewToErase: lynView)
            break
        case TrainDestination.lyon.rawValue:
            finishAnimation(lynView, withViewToErase: bdxView)
            break
        default:
            break
        }
    }
    
    func finishAnimation(_ view: UIView, withViewToErase eraseView: UIView){
        eraseView.alpha = 0
        UIView.animate(withDuration: 0.4) {
            self.versusImage.isHidden = true
            self.voteButton.isHidden = true
            eraseView.isHidden = true
        }
    }
    
    func customizeVoteButton() {
        voteButton.layer.cornerRadius = 4
    }
}
