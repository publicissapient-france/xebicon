import {Component, Input, OnInit, OnChanges} from "@angular/core";
import {ElevatorService} from "./elevator.service";
import {Credit} from "./elevator.model";

@Component({
  selector: 'elevator',
  template: require('./elevator.component.html'),
  styles: [require('./elevator.component.scss')],
  providers: [ElevatorService]
})
export class ElevatorComponent implements OnInit, OnChanges {
  @Input() enable: boolean = true;
  @Input() state: boolean = false;
  @Input() develop: boolean = false;

  credit: Credit;
  end: boolean;
  timeToEnd: number = 60;

  constructor(private elevatorService: ElevatorService) {
  }

  triggerEvents() {
    setTimeout(() => this.state = this.enable, 1500);

    if (this.enable) {
      setTimeout(() => this.end = true, this.timeToEnd * 1000);
    }
  }

  ngOnInit() {
    this.elevatorService.loadCredits()
      .subscribe(credit => {
        this.credit = credit;
        this.triggerEvents();
      });
  }

  ngOnChanges() {
    this.triggerEvents();
  }
}
