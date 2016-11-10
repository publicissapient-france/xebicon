//
//  KeynoteVoteCenter.swift
//  TrainSubscription
//
//  Created by Simone Civetta on 19/08/16.
//  Copyright © 2016 Xebia. All rights reserved.
//

import Foundation

open class KeynoteVoteCenter : NSObject {
  fileprivate var started = false
  fileprivate let router: Router<KeynoteState>
  fileprivate var userInfo: UserInfo
    public var delegate: KeynoteVoteCenterDelegate?
  
  open var viewController: UIViewController {
    return router.navigationController
  }
  
  override public init() {
    self.router = Router(navigationController: StoryboardScene.Storyboard.initialViewController())
    self.userInfo = UserInfo()
    self.userInfo.token = UDefaults.firebaseToken
    super.init()

    self.router.didNavigate = { [unowned self] controller in
      if var configurable = controller as? UserInfoConfigurable {
        configurable.userInfo = self.userInfo
      }
      
      if var routerAware = controller as? RouterAware {
        routerAware.router = self.router
      }
    }
  }
  
  open func requestStateChange(_ payload: KeynoteEvent) -> Bool {
    guard let state = payload.state else {
      return false
    }
    
    self.userInfo.message = payload.message
    self.userInfo.destination = payload.destination
    self.userInfo.obstacle = payload.obstacle

    self.navigate(state)
    
    return true
  }

    // for Objc
    @objc
    open func receivedRemoteNotification(_ payload: [AnyHashable: Any]) -> Bool {
        var keynoteEvent = KeynoteEvent()

        try? keynoteEvent.map(payload)

        return self.requestStateChange(keynoteEvent)
    }

    @objc
    public func setObjcDelegate(_ delegate: Any) {
        self.delegate = delegate as? KeynoteVoteCenterDelegate
    }

    func navigate(_ state:KeynoteState) {
        self.delegate?.didReceive(state: state, keynote: self)
        self.router.navigate(state)
    }
}

extension KeynoteVoteCenter {
    @objc
    public func start(_ token: String) {
        self.userInfo.token = token
        self.send(token)
        
        if !self.started {
            
            KeynoteState.get(callback: { (status, error) in
                guard let keynoteStatus = status else {
                    return
                }

                self.navigate(keynoteStatus)
            })
            
            self.delegate?.willStart(keynote: self)
        }
        
        self.started = true
    }
    
    fileprivate func send(_ fireBaseToken: String) {
        let register = RegisterableItem()
        register.post(with: UserInfo(token: fireBaseToken)) { (data, error) in
            if let error = error {
                print(error)
                return
            }
            print(data)
        }
    }
}
