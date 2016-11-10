import {Component} from "@angular/core";
import {select} from "ng2-redux/lib/index";
import {Observable} from "rxjs/Rx";

@Component({
  selector: 'dashboard',
  template: `
    <rails *ngIf="status !== 'TWITTER' && status !== 'KEYNOTE_WELCOME'"></rails>
    <twitter *ngIf="status === 'TWITTER'"></twitter>
    <welcome *ngIf="status === 'KEYNOTE_WELCOME'"></welcome>
    <vote class="screen-with-rails" *ngIf="status === 'VOTE' || status === 'VOTE_RESULT'"></vote>
    <train-departure class="screen-with-rails" *ngIf="status === 'TRAIN_DEPARTURE'"></train-departure>
    <train-departure-desc class="screen-with-rails" *ngIf="status === 'TRAIN_DESCRIPTION'"></train-departure-desc>
    <hot-deployment class="screen-with-rails" *ngIf="status === 'HOT_DEPLOYMENT'"></hot-deployment>
    <high-availability class="screen-with-rails" *ngIf="status === 'HIGH_AVAILABILITY'"></high-availability>
    <obstacle class="screen-with-rails" *ngIf="status === 'OBSTACLE_START'"></obstacle>
    <train-arrived class="screen-with-rails" *ngIf="status === 'TRAIN_ARRIVED'"></train-arrived>
    <publication class="screen-with-rails" *ngIf="status === 'PUBLICATION'"></publication>
    <elevator class="credits" *ngIf="status === 'CREDIT'" [enable]="true"></elevator>
  `,
  styles: [
    require('./dashboard.component.scss')
  ]
})
export class DashboardComponent {
  @select(['keynoteState', 'status']) keynoteState$: Observable<any>;
  status: string;

  constructor() {
    this.keynoteState$
      .subscribe(status => this.status = status);
  }
}
