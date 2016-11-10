import {Component, Input, Output, OnChanges, EventEmitter} from "@angular/core";

@Component({
  selector: 'passenger-item',
  template: require('./passenger-item.component.html'),
  styles: [
    require('./passenger-item.component.scss')
  ]
})
export class PassengerItemComponent implements OnChanges {
  @Input() passenger: any;
  @Input() passengerId: string;
  @Output() passengerGone = new EventEmitter();

  fade: string = 'default';
  timeout: any = undefined;

  ngOnChanges(changes) {
    if (changes.passenger.currentValue.fade === undefined) {
      this.timeout = setTimeout(() => {
        clearTimeout(this.timeout);
        this.fade = 'false';
        this.fadeOut();
      }, 100);
    } else if (changes.passenger.currentValue.fade === 'false') {
      this.fade = 'true';
      this.cleanTimeout()
    }
  }

  getAvatar = () => {
    const avatar = this.passenger.avatar;
    if (avatar !== '') {
      return `url(${avatar})`
    }
  };

  hasAvatar = () => {
    const avatar = this.passenger.avatar;
    return avatar && avatar !== ''
  };

  fadeOut = () => {
    this.timeout = setTimeout(() => {
      this.fade = 'true';
      clearTimeout(this.timeout);
      this.timeout = setTimeout(() => {
        this.handleEnd();
      }, 1100);
    }, this.getRandomTimer(3000, 17000));
  };

  getRandomTimer = (min, max) => {
    min = Math.ceil(min);
    max = Math.floor(max);
    return Math.floor(Math.random() * (max - min)) + min;
  };

  handleEnd = () => {
    this.passengerGone.emit(this.passengerId);
    this.cleanTimeout();
  };

  cleanTimeout = () => {
    if (this.timeout) {
      clearTimeout(this.timeout);
      this.timeout = undefined;
    }
  }
}
