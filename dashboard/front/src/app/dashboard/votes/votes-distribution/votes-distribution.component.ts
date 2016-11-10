import {Component, Input, ChangeDetectionStrategy, OnChanges} from "@angular/core";

@Component({
  selector: 'votes-distribution',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: require('./votes-distribution.component.html'),
  styles: [require('./votes-distribution.component.scss')]
})
export class VotesDistributionComponent implements OnChanges {
  @Input() data: Array<number>;
  @Input() showResults: boolean;

  initial: number = 30;
  percentage: number;

  constructor() {
  }

  compute(data: Array<number>) {
    if (data && data.length == 2) {
      let left = this.initial - (data[1] ? this.lo(data[1]) : 0);
      let right = this.initial - (data[0] ? this.lo(data[0]) : 0);
      if (left < 0) left = 0;
      if (right < 0) left = 0;
      return (left / (left + right)) * 100;
    }
    return 50;
  }

  lo = (x) => Math.log(x) / Math.log(1.23) + 1;

  get numFormat() {
    return Math.abs(50 - this.percentage) < 1 ? '1.0-1' : '1.0-0';
  }

  ngOnChanges() {
    this.percentage = this.compute(this.data);
  }
}
