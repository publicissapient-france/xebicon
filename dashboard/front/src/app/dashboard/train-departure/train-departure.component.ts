import {Component} from "@angular/core";
import {select} from "ng2-redux";
import {Observable} from "rxjs";

@Component({
  selector: 'train-departure',
  template: require('./train-departure.component.html'),
  styles: [require('./train-departure.component.scss')]
})
export class TrainDepartureComponent {
  @select(['keynoteState', 'trainId']) trainId$: Observable<any>;
  @select(['votes', 'distribution']) distribution$: Observable<any>;
  @select(['cameraConfig']) cameraConfig$: Observable<any>;

  message$: Observable<string> = this.trainId$
    .map(trainId => `<Train${trainId}> est au départ<Voie${trainId}>`);

  count$: Observable<number> = Observable.combineLatest(this.trainId$, this.distribution$)
    .filter(([trainId, distribution]) => trainId === 1 || trainId === 2)
    .filter(([trainId, distribution]) => distribution.size == 2)
    .map(([trainId, distribution]) => distribution.get(trainId - 1));

  cameraDepartureUrl$: Observable<string> = this.cameraConfig$
    .map(cameraConfig => cameraConfig.get('cameraDepartureUrl'));
}
