import {Component, Input, ChangeDetectionStrategy} from "@angular/core";

@Component({
  selector: 'tweet',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: require('./tweet.component.html'),
  styles: [
    require('./tweet.component.scss')
  ]
})
export class TweetComponent {
  @Input() username: string;
  @Input() avatarUrl: string;
  @Input() imageUrl: string;
  @Input() message: string;
  @Input() tweetedAt: number;

  formattedTweetedAt: string;

  ngOnChanges(changes) {
    if (changes.tweetedAt && changes.tweetedAt.currentValue) {
      this.formattedTweetedAt = 'il y a ' + this.fromNow(changes.tweetedAt.currentValue);
    }
  }

  private fromNow(time) {
    const duration = ((new Date()).getTime() - time) / 1000 | 0;

    if (duration < 60)           return 'quelques secondes';
    if (duration === 60)         return 'une minute';
    if (duration < 3600)         return (duration / 60 | 0) + ' minutes';      // 60 * 60
    if (duration === 3600)       return 'une heure';
    if (duration < 86400)        return (duration / 3600 | 0) +  ' heures';    // 3600 * 24
    if (duration === 86400)      return 'un jour';
    if (duration < 2592000)      return (duration / 86400 | 0) + ' jours';     // 86400 * 30
    if (duration === 2592000)    return 'un mois';
    if (duration < 946080000)    return (duration / 2592000 | 0) + ' mois';    // 2592000 * 365
    if (duration === 946080000)  return 'un an';

    return (duration / 946080000 | 0) + ' ans';
  }
}
