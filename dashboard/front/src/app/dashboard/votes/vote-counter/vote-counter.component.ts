import {Component, Input, ChangeDetectionStrategy, OnChanges} from "@angular/core";

@Component({
  selector: 'vote-counter',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: require('./vote-counter.component.html'),
  styles: [require('./vote-counter.component.scss')]
})
export class VoteCounterComponent implements OnChanges {
  @Input() data: any;
  counterVote: CounterVote;

  ngOnChanges() {
    this.counterVote = new CounterVote(this.data.ios, this.data.android, this.data.fb);
  }
}

class CounterVote {
  ios:number;
  android:number;
  fb:number;

  constructor(ios:number, android:number, fb:number) {
    this.ios = ios;
    this.android = android;
    this.fb = fb;
  }

  get iosPercentage():number {
    return this.percentage(this.ios) || 0;
  }

  get androidPercentage():number {
    return this.percentage(this.android) || 0;
  }

  get fbPercentage():number {
    return (100 - this.percentage(this.ios) - this.percentage(this.android)) || 0;
  }

  get iosWidth():number {
    return this.wight(this.ios) || 0;
  }

  get androidWidth():number {
    return this.wight(this.android) || 0;
  }

  get fbWidth():number {
    return this.wight(this.fb) || 0;
  }

  private wight(value:number):number {
    return Math.round(value / this.max() * 100);
  }

  private percentage(value:number):number {
    return Math.round(value / (this.ios + this.android + this.fb) * 100);
  }

  private max():number {
    return Math.max(this.ios, this.android, this.fb);
  }
}
