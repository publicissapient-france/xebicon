//
//  TrainDetails.swift
//  TrainSubscription
//
//  Created by Jeromin Lebon on 04/10/2016.
//  Copyright © 2016 Xebia. All rights reserved.
//

import Foundation

public struct TrainDetails {
    var color: UIColor?
    var hashtag: String?
    var imageTrain: UIImage?
    var imageStation: UIImage?
    var descriptionTrain: String?
    var station: String?
    
    static let Bordeaux = TrainDetails(color: Constant.Color.purpleColor,
                                       hashtag: Constant.Hashtag.bordeaux,
                                       imageTrain: UIImage(named: "bdx_round_icon", in: Bundle().currentBundle() , compatibleWith: nil),
                                       imageStation: UIImage(named: "bdx_station", in: Bundle().currentBundle() , compatibleWith: nil),
                                       descriptionTrain: "description",
                                       station: Constant.Station.bordeaux)
    
    static let Lyon = TrainDetails(color: Constant.Color.orangeColor,
                                   hashtag: Constant.Hashtag.lyon,
                                   imageTrain: UIImage(named: "lyn_round_icon", in: Bundle().currentBundle() , compatibleWith: nil),
                                   imageStation: UIImage(named: "lyn_station", in: Bundle().currentBundle() , compatibleWith: nil),
                                   descriptionTrain: "description",
                                   station: Constant.Station.lyon)
}

extension TrainDestination {
    func details() -> TrainDetails {
        switch self {
        case .bordeaux:
            return TrainDetails.Bordeaux
        case .lyon:
            return TrainDetails.Lyon
        }
    }
}
