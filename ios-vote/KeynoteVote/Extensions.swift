//
//  Extensions.swift
//  TrainSubscription
//
//  Created by Jeromin Lebon on 20/10/2016.
//  Copyright © 2016 Xebia. All rights reserved.
//

import Foundation

extension Bundle {
    func currentBundle() -> Bundle {
        let bundle: Bundle = Bundle(for: KeynoteVoteCenter.self)
        return bundle
    }
}
