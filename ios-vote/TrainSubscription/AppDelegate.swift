//
//  AppDelegate.swift
//  TrainSubscription
//
//  Created by Lana on 29/06/16.
//  Copyright © 2016 Xebia. All rights reserved.
//

import UIKit
import Firebase
import KeynoteVote

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {
    let voteCenter: KeynoteVoteCenter = KeynoteVoteCenter()
    fileprivate let queue = DispatchQueue(label: "KeynoteVoteCenterQueue", attributes: [])
    var window: UIWindow?
    
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplicationLaunchOptionsKey: Any]?) -> Bool {
        FIRApp.configure()
        self.voteCenter.delegate = self
        registerForPushNotifications(application)
        
        self.window!.makeKeyAndVisible()
        return true
    }
    
    // Remote notification
    
    func application(_ application: UIApplication, didRegister notificationSettings: UIUserNotificationSettings) {
        if notificationSettings.types != UIUserNotificationType() {
            application.registerForRemoteNotifications()
        }
    }
    
    func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        print(deviceToken)
        NotificationCenter.default.addObserver(self, selector: #selector(refresh), name: NSNotification.Name.firInstanceIDTokenRefresh, object: nil)
        
        print("register token apns")
        
        FIRInstanceID.instanceID().setAPNSToken(deviceToken, type: FIRInstanceIDAPNSTokenType.sandbox)
        self.refresh()
    }
    
    @objc fileprivate func refresh() {
        queue.async {
            guard let token = FIRInstanceID.instanceID().token() else {
                return
            }
            
            FIRMessaging.messaging().connect { (error) in
                if (error != nil) {
                    print("Unable to connect with FCM. \(error)")
                } else {
                    self.voteCenter.start(token)
                    print("Connected to FCM.")
                }
            }
        }
    }
    
    func application(_ application: UIApplication, didFailToRegisterForRemoteNotificationsWithError error: Error) {
        print("%@", error)
    }
    
    func application(_ application: UIApplication, didReceiveRemoteNotification userInfo: [AnyHashable: Any], fetchCompletionHandler completionHandler: @escaping (UIBackgroundFetchResult) -> Void) {
        print("%@", userInfo)
        
        var keynoteEvent = KeynoteEvent()
        
        do {
            try keynoteEvent.map(userInfo)
            voteCenter.requestStateChange(keynoteEvent)
        } catch {
            completionHandler(UIBackgroundFetchResult.noData)
        }
        
        completionHandler(UIBackgroundFetchResult.newData)
    }
}

private func registerForPushNotifications(_ application: UIApplication) {
    let notificationSettings = UIUserNotificationSettings(types: [.badge, .sound, .alert], categories: nil)
    
    application.registerUserNotificationSettings(notificationSettings)
}

extension AppDelegate : KeynoteVoteCenterDelegate {
    public func didReceive(state: KeynoteState, keynote: KeynoteVoteCenter) {
        
    }

    public func willStart(keynote: KeynoteVoteCenter) {
        self.window?.rootViewController = keynote.viewController
    }
}

