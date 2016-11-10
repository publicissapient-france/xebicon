import {Component, Input, OnChanges} from "@angular/core";

@Component({
  selector: 'station-message',
  template: `
    <div class="content station-{{stationId}}">
      <station-icon [stationId]="stationId"></station-icon>
      <station [stationId]="stationId"></station>
      <span>{{internalMessage}}</span>
    </div>
  `,
  styles: [require('./station-message.component.scss')]
})
export class StationMessageComponent implements OnChanges {
  stationId: number = 1;
  internalMessage: string = '';

  @Input()
  message: string = '';


  ngOnChanges(c) {
    this.stationId = this.message.indexOf('<Station1>') !== -1 ? 1 : 2;

    this.internalMessage = this.message.replace(/<[\w\d]*>/g, '');
  }

}
