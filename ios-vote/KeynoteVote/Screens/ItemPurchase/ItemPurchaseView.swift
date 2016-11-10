//
//  ItemPurchaseView.swift
//  TrainSubscription
//
//  Created by Jeromin Lebon on 07/10/2016.
//  Copyright © 2016 Xebia. All rights reserved.
//

import Foundation

protocol ItemPurchaseViewDelegate: class {
    func didChoose(_ wine: PurchasableItem)
    func isSelected(_ wine: PurchasableItem) -> Bool
    func sendChoice()
}

class ItemPurchaseView: UIView {
    
    @IBOutlet weak var chooseButton: UIButton!
    @IBOutlet weak var wineMargaux: UIImageView!
    @IBOutlet weak var winePauillac: UIImageView!
    @IBOutlet weak var winePessac: UIImageView!
    
    @IBOutlet weak var margauxView: UIView!
    @IBOutlet weak var pauillacView: UIView!
    @IBOutlet weak var pessacView: UIView!
    
    @IBOutlet weak var boughtLabel: UILabel!
    @IBOutlet weak var banner: Banner!
    
    var wineSelected: PurchasableItem?
    weak var delegate: ItemPurchaseViewDelegate?
    
    override func awakeFromNib() {
        customizeChooseButton()
        self.wineMargaux.image = self.wineMargaux.image?.withRenderingMode(.alwaysTemplate)
        self.winePessac.image = self.winePessac.image?.withRenderingMode(.alwaysTemplate)
        self.winePauillac.image = self.winePauillac.image?.withRenderingMode(.alwaysTemplate)
        refreshBottles()
    }
    
    @IBAction func MargauxSelected() {
        self.delegate?.didChoose(.Margaux)
        UIView.animate(withDuration: 0.4) {
            self.refreshBottles()
        }
    }
    
    @IBAction func PauillacSelected() {
        self.delegate?.didChoose(.Pauillac)
        UIView.animate(withDuration: 0.4) {
            self.refreshBottles()
        }
    }
    
    @IBAction func PessacSelected() {
        self.delegate?.didChoose(.Pessac)
        UIView.animate(withDuration: 0.4) {
            self.refreshBottles()
        }
    }
    
    @IBAction func SendChosenBottle(_ sender: UIButton) {
        guard let delegate = self.delegate else {
            return
        }
        UIView.animate(withDuration: 1.0, delay: 0.0, options: [], animations: {
            self.boughtLabel.alpha = 1.0
        }) { (finish) in
            UIView.animate(withDuration: finish ? 0.4 : 0.0, delay: 0.0, options: [], animations: {
                self.boughtLabel.alpha = 0.0
            })
        }
        self.delegate?.sendChoice()
    }
    
    func refreshBottles() {
        guard let delegate = self.delegate else {
            return
        }
        self.wineMargaux.tintColor = delegate.isSelected(.Margaux) ? Constant.Color.bordeauxColor : UIColor.black
        self.winePauillac.tintColor = delegate.isSelected(.Pauillac) ? Constant.Color.bordeauxColor : UIColor.black
        self.winePessac.tintColor = delegate.isSelected(.Pessac) ? Constant.Color.bordeauxColor : UIColor.black
        chooseButton.isHidden = false
    }
    
    func customizeChooseButton() {
        chooseButton.layer.cornerRadius = 4
        chooseButton.layer.backgroundColor = Constant.Color.purpleColor.cgColor
    }
}
