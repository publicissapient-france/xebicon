//
//  PurchasableItem.swift
//  TrainSubscription
//
//  Created by Simone Civetta on 19/08/16.
//  Copyright © 2016 Xebia. All rights reserved.
//

import Foundation

enum PurchasableItem: String {
    case Pauillac = "pauillac"
    case Margaux = "margaux"
    case Pessac = "pessac"
}

extension PurchasableItem: JSONSerializable {
    fileprivate struct Keys {
        static let media = "media"
        static let article = "article"
    }
    
    func JSONDictionary() -> [AnyHashable : Any] {
        return [
            Keys.media: "IOS",
            Keys.article: self.rawValue
        ]
    }
}

extension PurchasableItem: Postable {

    var URL: Foundation.URL {
        return Endpoint.purchase.URL as URL
    }
}
