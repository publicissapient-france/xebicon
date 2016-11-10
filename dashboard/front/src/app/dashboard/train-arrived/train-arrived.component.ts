import {Component} from "@angular/core";
import {select} from "ng2-redux";
import {Observable} from "rxjs";

@Component({
  selector: 'train-arrived',
  template: require('./train-arrived.component.html'),
  styles: [require('./train-arrived.component.scss')]
})
export class TrainArrivedComponent {
  @select(['keynoteState', 'trainId']) trainId$: Observable<any>;
  @select(['cameraConfig']) cameraConfig$: Observable<any>;

  message$: Observable<string> = this.trainId$
    .map(trainId => `<Train${trainId}> est arrivé à la station Xebicon`);

  cameraArrivedUrl$: Observable<string> = this.cameraConfig$
    .map(cameraConfig => cameraConfig.get('cameraArrivedUrl'));
}
