//
//  PostService.swift
//  TrainSubscription
//
//  Created by Simone Civetta on 19/08/16.
//  Copyright © 2016 Xebia. All rights reserved.
//

import Foundation

public typealias PostCallback = (_ data: Data?, _ error: Error?) -> ()

class PostService {
  
  fileprivate enum ErrorStatus: Error {
    case noDataToSend
    case invalidResponse
    case unacceptableStatusCode
  }
  
  static func send(_ URL: Foundation.URL, payload: [AnyHashable : Any], callback: @escaping PostCallback) {    
    do {
      let request = NSMutableURLRequest(url: URL as URL)
      request.httpBody = try JSONSerialization.data(withJSONObject: payload, options: [])
      request.httpMethod = "POST"
      request.setValue("application/json", forHTTPHeaderField: "Content-Type")
      request.setValue("*/*", forHTTPHeaderField: "Accept")
      request.setValue("gzip, deflate", forHTTPHeaderField: "Accept-Encoding")
      request.setValue("no-cache", forHTTPHeaderField: "Cache-Control")
      
      URLSession.shared.dataTask(with: request as URLRequest) { (data, response, error) in
        if let error = error {
          callback(nil, error)
          return
        }
        
        guard let response = (response as? HTTPURLResponse) else {
          callback(nil, ErrorStatus.invalidResponse)
          return
        }
        
        switch response.statusCode {
        case 200..<300:
          callback(data as Data?, nil)
          
        default:
          callback(nil, ErrorStatus.unacceptableStatusCode)
        }
        
      }.resume()
      
    } catch {
      callback(nil, error)
    }
  }
}
