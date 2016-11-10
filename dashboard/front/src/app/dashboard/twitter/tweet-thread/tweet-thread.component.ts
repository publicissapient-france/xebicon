import {Component, Input, ChangeDetectionStrategy} from "@angular/core";

@Component({
  selector: 'tweet-thread',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: require('./tweet-thread.component.html'),
  styles: [
    require('./tweet-thread.component.scss')
  ]
})
export class TweetThreadComponent {
  @Input() thread: Array<any>;

  tweetIdentity(index, tweet) {
    return tweet.tweetId;
  }
}
