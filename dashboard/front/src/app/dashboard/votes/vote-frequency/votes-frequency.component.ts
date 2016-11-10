import {Component, Input, Output, EventEmitter} from "@angular/core";

@Component({
  selector: 'votes-frequency',
  template: require('./votes-frequency.component.html'),
  styles: [`
    :host {
      display: inline-block;
    }
    
    .votes-frequency-container {
      text-align: center;
    }
  `]
})
export class VotesFrequencyComponent {
  @Input() data: Array<any>;
  @Output() voteExpired = new EventEmitter();
}
