//
//  RegisterableItem.swift
//  TrainSubscription
//
//  Created by Julien on 19/09/2016.
//  Copyright © 2016 Xebia. All rights reserved.
//

import Foundation

public struct RegisterableItem {
    public init() {}
}

extension RegisterableItem: JSONSerializable {
    fileprivate struct Keys {
        static let userId = "userId"
    }
    
    func JSONDictionary() -> [AnyHashable : Any] {
        return [:]
    }
}

extension RegisterableItem: Postable {

    var URL: Foundation.URL {
        return Endpoint.register.URL as URL
    }
}
