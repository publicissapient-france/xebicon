import {Component, Input, Output, EventEmitter} from "@angular/core";

@Component({
  selector: 'votes-frequency-item',
  template: require('./votes-frequency-item.component.html'),
  styles: [require('./votes-frequency-item.component.scss')]
})
export class VotesFrequencyItemComponent {
  @Input() data: any;
  @Output() voteExpired = new EventEmitter();

  timeout: any = undefined;
  splash: boolean = false;

  constructor() {
  }

  handleEnd = () => {
    this.voteExpired.emit(this.data);
    this.timeout = undefined;
  };

  ngOnChanges() {
    if (this.data.get('show')) {
      this.splash = true;
      this.timeout = setTimeout(() => this.splash = false, 1000);
      this.timeout = setTimeout(this.handleEnd, 2000);
    } else {
      this.splash = false;
      if (this.timeout) {
        clearTimeout(this.timeout);
        this.timeout = undefined;
      }
    }
  }
}
