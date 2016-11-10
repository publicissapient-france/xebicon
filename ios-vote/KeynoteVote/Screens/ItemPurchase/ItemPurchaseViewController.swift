//
//  ItemPurchaseViewController.swift
//  TrainSubscription
//
//  Created by Simone Civetta on 19/08/16.
//  Copyright © 2016 Xebia. All rights reserved.
//

import Foundation
import UIKit

class ItemPurchaseViewController : UIViewController, UserInfoConfigurable, RouterAware, ItemPurchaseViewDelegate {
  
  var userInfo: UserInfo?
  var router: Router<KeynoteState>!
  var itemPurchaseView: ItemPurchaseView{return view as! ItemPurchaseView}
    var itemPurchase: PurchasableItem?
  
  override func viewDidLoad() {
    self.navigationController?.navigationBar.barTintColor = Constant.Color.purpleColor
    self.navigationController?.navigationBar.isTranslucent = false
    itemPurchaseView.delegate = self
    guard let trainDetails = userInfo?.destination else {
        return
    }
    self.itemPurchaseView.banner.update(with: trainDetails)
  }
  
  fileprivate func purchaseItem(_ item: PurchasableItem) {
    guard let userInfo = userInfo else {
      return
    }
    
    item.post(with: userInfo) { (data, error) in
      if let error = error {
        print(error)
      }
      
      print(data)
    }
  }
    
    func didChoose(_ wine: PurchasableItem) {
        self.itemPurchase = wine
    }
    
    func isSelected(_ wine: PurchasableItem) -> Bool {
        return self.itemPurchase == wine ? true : false
    }
    
    func sendChoice() {
        if let wine = self.itemPurchase {
            purchaseItem(wine)
        }
    }
}
