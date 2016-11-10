//
//  KeynoteUIPresenter.swift
//  TrainSubscription
//
//  Created by pjechris on 18/10/16.
//  Copyright © 2016 Xebia. All rights reserved.
//

import Foundation

public protocol KeynoteVoteCenterDelegate {
    func willStart(keynote: KeynoteVoteCenter)
    func didReceive(state: KeynoteState, keynote: KeynoteVoteCenter)
}
