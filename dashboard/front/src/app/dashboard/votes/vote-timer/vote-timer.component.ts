import {Component, Input, OnChanges} from "@angular/core";

@Component({
  selector: 'vote-timer',
  template: `
    <span *ngIf="timer > 0">{{ timer | toTime }}</span>
    <span *ngIf="timer < 1">Vote ended</span>
  `,
  styles: [require('./vote-timer.component.scss')]
})
export class VoteTimerComponent implements OnChanges {
  @Input() voteDuration: number;

  timer: number = -1;
  interval: any = undefined;

  handler = () => {
    this.timer--;
    if (this.timer < 1) {
      clearInterval(this.interval);
      this.interval = undefined;
    }
  };

  ngOnChanges() {
    if (this.voteDuration) {
      this.timer = this.voteDuration;

      if (!this.interval) {
        this.interval = setInterval(this.handler, 1000);
      }
    } else {
      clearInterval(this.interval);
      this.interval = undefined;
    }
  }
}
