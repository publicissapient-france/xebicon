//
//  GetService.swift
//  TrainSubscription
//
//  Created by Jeromin Lebon on 21/10/2016.
//  Copyright © 2016 Xebia. All rights reserved.
//

import Foundation

class GetService {
    
    static func send(_ URL: Foundation.URL, callback: @escaping (_ data: [AnyHashable:Any]?, _ error: Error?) -> ()){
        do{
            let request = NSMutableURLRequest(url: URL as URL)
            URLSession.shared.dataTask(with: request as URLRequest) { (data, response, error) in
                if let error = error {
                    callback(nil, error)
                    return
                }
                
                guard let response = (response as? HTTPURLResponse) else {
                    callback(nil, error)
                    return
                }
                
                switch response.statusCode {
                case 200..<300:
                    do {
                        guard let result = try JSONSerialization.jsonObject(with: data!, options: JSONSerialization.ReadingOptions()) as? [AnyHashable: Any] else {
                            return
                        }
                        callback(result, nil)
                    } catch {
                        callback(nil, error)
                    }
                default:
                    callback(nil, error)
                }
                
                }.resume()
            
        } catch {
            callback(nil, error)
        }
    }
}
