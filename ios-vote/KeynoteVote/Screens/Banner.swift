//
//  Banner.swift
//  TrainSubscription
//
//  Created by Jeromin Lebon on 25/10/2016.
//  Copyright © 2016 Xebia. All rights reserved.
//

import Foundation

@IBDesignable class Banner: UIView {
    private var destination: UILabel! = UILabel()
    class override var requiresConstraintBasedLayout: Bool { return true }
    
    #if !TARGET_INTERFACE_BUILDER
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        self.build()
    }
    #else
    override func prepareForInterfaceBuilder() {
        self.build()
        destination.text = "#ViaBdx"
        self.backgroundColor = Constant.Color.purpleColor
    }
    #endif

    func update(with trainDestination: TrainDestination) {
        destination.text = trainDestination.details().hashtag
        self.backgroundColor = trainDestination.details().color
    }
    
    func build() {
        destination.translatesAutoresizingMaskIntoConstraints = false
        destination.font = UIFont.systemFont(ofSize: 36)
        destination.textAlignment = NSTextAlignment.center
        destination.textColor = UIColor.white
        self.addSubview(destination)
        
        let leadingConstraint = destination.leadingAnchor.constraint(equalTo: self.leadingAnchor)
        
        let trailingConstraint = destination.rightAnchor.constraint(equalTo: self.rightAnchor)
        
        let topConstraint = NSLayoutConstraint(item: destination, attribute: .top, relatedBy: .equal, toItem: self, attribute: .top, multiplier: 1, constant: 8)

        let bottomConstraint = NSLayoutConstraint(item: destination, attribute: .bottom, relatedBy: .equal, toItem: self, attribute: .bottom, multiplier: 1, constant: -8)
        
        NSLayoutConstraint.activate([leadingConstraint, trailingConstraint, topConstraint, bottomConstraint])
    }
}
