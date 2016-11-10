
import {Component, Input} from "@angular/core";

@Component({
  selector: 'station',
  template: `
    <div class="station-{{stationId}}">
      <span *ngIf="stationId == 1">Gare de Bordeaux</span>
      <span *ngIf="stationId == 2">Gare Lyon</span>
    </div>
  `,
  styles: [require('./station.component.scss')]
})
export class StationComponent {
  @Input()
  stationId: number = 1;
}
