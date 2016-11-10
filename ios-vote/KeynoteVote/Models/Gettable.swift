//
//  Getable.swift
//  TrainSubscription
//
//  Created by Jeromin Lebon on 21/10/2016.
//  Copyright © 2016 Xebia. All rights reserved.
//

import Foundation

protocol Gettable {
    static var URL: Foundation.URL { get }
    static func get(callback: @escaping (_ data: Self?, _ error: Error?) -> ())
}

extension Gettable where Self: JSONDeserialization {
    static func get(callback: @escaping (_ data: Self?, _ error: Error?) -> ()) {
        GetService.send(self.URL) { json, error in
            guard let json = json else {
                return callback(nil, error)
            }
            
            callback(Self(from: json), nil)
        }
    }
}
