import {Component} from "@angular/core";
import {select, NgRedux} from "ng2-redux/lib/index";
import {Observable} from "rxjs/Observable";

@Component({
  selector: 'vote',
  template: require('./votes.component.html'),
  styles: [
    require('./votes.component.scss')
  ]
})
export class VoteComponent {
  @select(['votes', 'status']) status$: Observable<any>;
  @select(['votes', 'distribution']) distribution$: Observable<any>;
  @select(['volatile', 'votesVolatile']) votesVolatile$: Observable<any>;
  @select(['votes', 'voteDuration']) voteDuration$: Observable<number>;
  @select(['votes', 'counter']) voteCounter$: Observable<any>;

  showResults: boolean = false;
  votesDistribution: Array<number> = [0, 0];
  voteDuration: number = undefined;
  votesVolatileLeft: any = [];
  votesVolatileRight: any = [];
  winnerTrainId: number = 1;

  constructor(private ngRedux: NgRedux<any>) {
    this.status$
      .filter(state => !!state)
      .subscribe(status => {
        this.showResults = status === 'VOTE_RESULT';
      });

    this.votesVolatile$
      .filter(data => !!data)
      .subscribe(votes => {
        this.votesVolatileLeft = votes.get('left');
        this.votesVolatileRight = votes.get('right');
      });

    this.distribution$
      .filter(data => !!data)
      .subscribe(votesDistribution => {
        this.votesDistribution = votesDistribution;
        this.winnerTrainId = votesDistribution.get(0) > votesDistribution.get(1) ? 1 : 2;
      });

    this.voteCounter$
      .subscribe(data => {
        console.log(data);
      });
  }

  onVoteExpired(item, trainId) {
    this.ngRedux.dispatch({type: 'VOTE_ERASE', payload: {id: trainId, index: item.get('index')}})
  }
}
