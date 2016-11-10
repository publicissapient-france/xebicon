import {Component, Input, Output, EventEmitter} from "@angular/core";

@Component({
  selector: 'passenger-list',
  template: require('./passenger-list.component.html'),
  styles: [require('./passenger-list.component.scss')]
})
export class PassengerListComponent {
  @Input() passengers: Array<any>;
  @Output() passengerItemGone = new EventEmitter();

  onPassengerGone = (passengerId) => {
    this.passengerItemGone.emit(passengerId);
  }
}
