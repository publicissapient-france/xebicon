import {Component, OnInit} from "@angular/core";
import {select} from "ng2-redux/lib/index";
import {Observable} from "rxjs/Observable";

@Component({
  selector: 'obstacle',
  template: require('./obstacle.component.html'),
  styles: [require('./obstacle.component.scss')]
})
export class ObstacleComponent implements OnInit {
  @select(['obstacle']) obstacle$: Observable<any>;
  @select(['keynoteState', 'trainId']) trainId$: Observable<any>;
  @select(['cameraConfig']) cameraConfig$: Observable<any>;

  private obstacleUrl: string;
  private cameraConfig;

  obstacleUrlMap: Object = {
    cow: '../../../assets/img/warning-cow.png',
    horse: '../../../assets/img/warning-horse.png',
    pig: '../../../assets/img/warning-pig.png',
    rabbit: '../../../assets/img/warning-rabbit.png',
    chicken: '../../../assets/img/warning-chicken.png',
    _unknown: '../../../assets/img/warning.svg',
    _ok: ''
  };

  message$: Observable<string> = this.trainId$
    .map(trainId => `<Train${trainId}> détecte  un obstacle`);

  ngOnInit() {
    this.obstacle$
      .filter(data => !!data)
      .map(data => data.get('obstacleType') ? data.get('obstacleType').toLowerCase() : '_ok')
      .map(type => this.obstacleUrlMap[type] !== undefined ? type : '_unknown')
      .map(type => {
        console.log('>>>', type);
        return this.obstacleUrlMap[type] !== undefined ?  this.obstacleUrlMap[type] : this.obstacleUrlMap['_unknown'];
      })
      .subscribe(obstacleUrl => {
        this.obstacleUrl = obstacleUrl;
      });

    this.cameraConfig$
      .map(cameraConfig => cameraConfig.toJS())
      .subscribe(cameraConfig => {
        this.cameraConfig = {
          cameraObstacleUrl: cameraConfig ? cameraConfig.cameraObstacleUrl : '',
          cameraObstacleFlux1Url: cameraConfig ? cameraConfig.cameraObstacleFlux1Url : ''
        };
      });
  }
}
