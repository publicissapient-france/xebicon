import {Component, Input} from "@angular/core";

@Component({
  selector: 'station-icon',
  template: require('./station-icon.component.html'),
  styles: [require('./station-icon.component.scss')]
})
export class StationIconComponent {

  @Input()
  stationId: number = 1;
}
