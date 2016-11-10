import {Component, OnInit} from "@angular/core";
import {select} from "ng2-redux";
import {Observable} from "rxjs";
import {Range, Map} from "immutable";

@Component({
  selector: 'train-departure-desc',
  template: require('./train-description.component.html'),
  styles: [require('./train-description.component.scss')]
})
export class TrainDescriptionComponent implements OnInit {
  @select(['keynoteState', 'trainId']) trainId$: Observable<any>;
  @select(['trainPosition']) trainPosition$: Observable<any>;
  @select(['votes', 'passengers']) passengers$: Observable<any>;

  message$: Observable<string>;
  passengersTrain: any;
  passengersTrainRows: any;
  maxChunkTrain: any;
  passengerNextId: any;

  ngOnInit() {
    this.message$ = Observable.combineLatest(this.trainId$, this.trainPosition$)
      .map(([trainId, position]) => {
        const pos:string = position.getIn(['trains', (trainId === 1 ? 0 : 1), 'position']);

        switch (pos) {
          case 'bordeaux':
            return `<Train${trainId}> est en route vers <Station1>`;
          case 'lyon':
            return `<Train${trainId}> est en route vers <Station2>`;
          case 'xebicon':
            return `<Train${trainId}> est en route vers <XebiConImg>`;
        }

        return `<Train${trainId}> est en route vers <XebiConImg>`;
      });

    this.trainId$
      .filter(trainId => !!trainId)
      .subscribe(trainId => {
        this.passengers$
          .filter(state => !!state)
          .subscribe(passengers => {
            this.passengersTrain = passengers.get(trainId - 1);
            this.maxChunkTrain = Math.min(this.passengersTrain.count(), 30);
            this.passengersTrainRows = Range(0, this.maxChunkTrain, 10)
              .map(chunkStart => this.passengersTrain.slice(chunkStart, chunkStart + 10))
              .toJS();

          })
      })
  }

  onPassengerItemGone = (passengerId) => {
    const passengerIndexes = passengerId.match(/(^\d{1,3})-(\d{1,3}$)/);
    const passengerRowIndex = passengerIndexes[1];
    const passengerItemIndex = passengerIndexes[2];

    if (this.passengerNextId === undefined) {
      this.passengerNextId = this.maxChunkTrain;
    }
    this.passengerNextId++;
    if (this.passengerNextId >= this.passengersTrain.count()) {
      this.passengerNextId = 0;
    }

    const random = this.getRandomPassenger(this.passengerNextId, this.passengersTrain.count());
    const passengerNew = this.passengersTrain.get(random).toJS();

    this.passengersTrainRows[passengerRowIndex][passengerItemIndex] = passengerNew;
  };

  getRandomPassenger = (min, max) => {
    min = Math.ceil(min);
    max = Math.floor(max);
    return Math.floor(Math.random() * (max - min)) + min;
  };

}
