import {Component} from "@angular/core";
import {fromJS} from "immutable";

@Component({
  selector: 'kitchen-sink',
  template: require('./kitchen-sink.component.html'),
  styles: [require('./kitchen-sink.component.scss')]
})
export class KitchenSinkComponent {
  constructor() {
   setInterval(this.addTweet.bind(this), 1500);
  }

  // Youtube
  ytId = 'v_bVRt94UlM';
  private player;
  private ytEvent;

  ytOnStateChange(event) {
    this.ytEvent = event.data;
  }

  ytSavePlayer(player) {
    this.player = player;
    this.player.playVideo();
  }

  ytPlayVideo() {
    console.log('PLAY');
    this.player.playVideo();
  }

  ytPauseVideo() {
    console.log('PAUSE');
    this.player.pauseVideo();
  }

  // vote timer
  voteDuration: number = 180;

  // Tweet threads
  private threads = fromJS([[], [], []]);
  private tweets = fromJS([
    {
      username: 'abachar',
      avatarUrl: 'https://pbs.twimg.com/profile_images/734090355344302080/RyMLTD_y_bigger.jpg',
      imageUrl: 'http://lorempixel.com/640/360/technics',
      message: 'Thanks @XebiaFr for this very interesting #XebiConFr day. I am leaving with good ideas and added knowledge.',
      tweetedAt: 1473606653715
    },
    {
      username: 'zenorocha',
      avatarUrl: 'https://pbs.twimg.com/profile_images/472461686071246848/NCdcpl65_bigger.jpeg',
      imageUrl: 'http://lorempixel.com/640/360/business',
      message: '#XebiConFr c\'est parti pour "dessine moi un nuage"',
      tweetedAt: 1473606653715
    },
    {
      username: 'addyosmani',
      avatarUrl: 'https://pbs.twimg.com/profile_images/725034709533728770/ZVaJZ_tu_bigger.jpg',
      message: '#TransformationDigitale : "On ne peut pas faire de #Digital sans avoir une stratégie logicielle." #XebiConFr',
      tweetedAt: 1473606653715
    },
    {
      username: 'guilletmichel',
      avatarUrl: 'https://pbs.twimg.com/profile_images/607159616800215041/29LQjmc-_bigger.jpg',
      imageUrl: 'http://lorempixel.com/640/360/abstract',
      message: 'Thanks @XebiaFr for this very interesting #XebiConFr day. I am leaving with good ideas and added knowledge.',
      tweetedAt: 1473606653715
    }
  ]);
  private tweetNextThread = 0;

  private addTweet() {
    const threadIndex = this.tweetNextThread++ % this.threads.size;
    const randomTweet = (Math.random() * 999) % this.tweets.size;

    this.threads = this.threads.updateIn([threadIndex], list => list.unshift(
      this.tweets.get(randomTweet).set('tweetId', this.tweetNextThread)
    ).take(5));
  }

  // votes frequency item
  votesFrequencyItem:any = fromJS({show: false});

  votesFrequencyItemShow(media) {
    if (media === 'FB') {
      this.votesFrequencyItem = this.votesFrequencyItem
        .set('avatar', 'https://pbs.twimg.com/profile_images/3446825772/99e6bfa9e85d229904be34c5f388a99f_bigger.jpeg');
    }

    if (media === 'TWITTER') {
      this.votesFrequencyItem = this.votesFrequencyItem
        .set('avatar', 'https://pbs.twimg.com/profile_images/3279904548/08fc85c53720dcee3c2a71300c63dee7_normal.jpeg');
    }

    this.votesFrequencyItem = this.votesFrequencyItem
      .set('show', true)
      .set('media', media);
  }

  onVoteItemExpired() {
    this.votesFrequencyItem = this.votesFrequencyItem
      .set('show', false)
      .set('media', '')
      .set('avatar', '');
  }

  // votes frequency
  media:Array<string> = ['SMS', 'TWITTER', 'IOS', 'ANDROID', 'FB'];
  avatars:Array<string> = [
    'https://pbs.twimg.com/profile_images/738437941962412032/x6eL42WZ_bigger.jpg',
    'https://pbs.twimg.com/profile_images/743405421197549568/n51LWnEf_bigger.jpg',
    'https://pbs.twimg.com/profile_images/586828037582409728/LfVmqvZz_bigger.jpg',
    'https://pbs.twimg.com/profile_images/3446825772/99e6bfa9e85d229904be34c5f388a99f_bigger.jpeg',
    'https://pbs.twimg.com/profile_images/3279904548/08fc85c53720dcee3c2a71300c63dee7_normal.jpeg',
    'https://pbs.twimg.com/profile_images/878025000/marketingvacaturestwitter_bigger.gif'
  ];
  getAvatar = () => this.avatars[Math.round(Math.random() * this.avatars.length)];
  votesFrequency:any = fromJS(this.createBasket(7));
  interval:any = undefined;

  createBasket(size) {
    let res = [];
    for (let i = 0; i < size; i++) {
      res.push({show: false, index: i});
    }
    return res;
  }

  votesFrequencyRandom() {
    const indexMedia = Math.floor(Math.random() * this.media.length);
    const index = Math.floor(Math.random() * this.votesFrequency.size);
    if (!this.votesFrequency.getIn([index, 'show'])) {
      this.votesFrequency = this.votesFrequency.updateIn([index], item => item
        .set('show', true)
        .set('media', this.media[indexMedia])
        .set('avatar', Math.round(Math.random() * 3) === 1 ? this.getAvatar() : ''));
    }
  }

  onVoteExpired(item) {
    const index = item.get('index');
    this.votesFrequency = this.votesFrequency.updateIn([index], item => item
      .set('show', false)
      .set('media', '')
      .set('avatar', '')
    );
  };

  votesFrequencyRandomStart() {
    if (!this.interval) {
      this.interval = setInterval(() => this.votesFrequencyRandom(), 200);
    }
  }

  votesFrequencyRandomStop() {
    this.interval && clearInterval(this.interval);
    this.interval = undefined;
  }

  // votes
  votesDistributionModel:Array<number> = [30, 30];
  votesDistribution:Array<number> = [30, 30];

  votesDistributionRandom() {
    const percentage = Math.random() * 100;
    this.votesDistribution = [percentage, 100 - percentage]
  }

  votesDistributionChange(item) {
    this.votesDistribution = [this.votesDistributionModel[0], this.votesDistributionModel[1]];
  }

  // Shops
  shopData = {
    shops: {"items": {"pauillac": 1, "margaux": 2, "pessac": 3}},
    buy: {"items": {"pauillac": 7, "margaux": 2, "pessac": 7}},
  };

  clusters = [{"type": "cloud", "active": true, "apps": [{"name": "bar-app"}]},
    {"type": "local", "active": true, "apps": [{"name": "bar-app"}, {"name": "database"}, {"name": "database"}]}];

  // Passengers
  passengerData:any = {
    media: "ANDROID",
    avatar: "https://pbs.twimg.com/profile_images/2717434746/56ddc77ba7ee208526866247108b71eb_bigger.png",

  };

  refreshPassenger() {
    this.passengerData = {
      media: 'IOS',
      avatar: '',

    };
  }

  passengersData: Array<any> = [[
    {media: 'FB', avatar: "https://pbs.twimg.com/profile_images/2717434746/56ddc77ba7ee208526866247108b71eb_bigger.png"},
    {media: 'ANDROID', avatar: "https://pbs.twimg.com/profile_images/755315452981673984/u0qV1kbU_400x400.jpg"},
    {media: 'ANDROID', avatar: ""},
    {media: 'TWITTER', avatar: ""},
    {media: 'FB', avatar: "https://pbs.twimg.com/profile_images/3279904548/08fc85c53720dcee3c2a71300c63dee7_400x400.jpeg"},
    {media: 'ANDROID', avatar: "https://pbs.twimg.com/profile_images/629704293701042176/Sq3ogV3s_400x400.jpg"},
    {media: 'SMS', avatar: ""},
    {media: 'TWITTER', avatar: "https://pbs.twimg.com/profile_images/586828037582409728/LfVmqvZz_400x400.jpg"},
    {media: 'IOS', avatar: ""},
    {media: 'FB', avatar: ""}
  ]];

  onPassengerItemGone(passengerId) {
    //console.log('passenger gone', passengerId)
  }

  // credit elevator in develop mode
  elevatorDevelop:boolean = true;
  elevatorEnable:boolean = false;
}
