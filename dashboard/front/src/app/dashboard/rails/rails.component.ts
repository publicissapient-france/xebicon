import {Component, ViewChildren, AfterViewInit, QueryList} from "@angular/core";
import {select} from "ng2-redux/lib/index";
import {Observable} from "rxjs/Rx";
import {Train} from "./train.models";
import {TrainDirective} from "./train.directive";

@Component({
  selector: 'rails',
  template: require('./rails.component.v2.html'), //require('./rails.component.html'),
  styles: [`
    :host {
      text-align: center;
      display: block;
      background-color: white;
      padding-top: 24px;
      box-sizing: border-box;
    }
    @keyframes fadeIn { 
        from { opacity: 0; } 
    }
    .animate-train {
        animation: fadeIn 1s infinite alternate;
    }
  `]
})
export class RailsComponent implements AfterViewInit {
  data: string;
  currentTrainId: number;

  @select(['trainPosition', 'trains', '0']) trains0$: Observable<any>;
  @select(['trainPosition', 'trains', '1']) trains1$: Observable<any>;

  @select(['keynoteState', 'status']) obstacle$: Observable<any>;
  @select(['keynoteState', 'trainId']) trainId$: Observable<any>;

  @ViewChildren(TrainDirective) trains: QueryList<TrainDirective>;


  constructor() {
  }

  ngAfterViewInit() {
    this.initObservable(this.trains0$);
    this.initObservable(this.trains1$);
    this.trainId$
      .filter(trainId => !!trainId)
      .subscribe(trainId => this.currentTrainId = trainId);
    this.obstacle$
      .filter(obstacle => !!obstacle)
      .filter(status => status === 'OBSTACLE_START')
      .subscribe(obstacle => {
        this.showTrain({trainId: this.currentTrainId, position: 'obstacle'}, this.trains);
      });
  }

  private initObservable(trains$: Observable<any>) {
    trains$
      .filter(train => !!train)
      .map(train => train.toJS())
      .subscribe(train => this.showTrain(train, this.trains));
  }

  private showTrain(train: any, trains: QueryList<TrainDirective>) {
    trains
      .filter(item => item.trainId === train.trainId)
      .forEach(wagon => {
        if (wagon.position === train.position) {
          wagon.show();
        } else {
          wagon.hide();
        }
      });
  }
}


