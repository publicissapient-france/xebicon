import {Component, Input} from "@angular/core";

@Component({
  selector: 'train-info',
  template: require('./train-info.component.html'),
  styles: [require('./train-info.component.scss')]
})
export class TrainInfoComponent {
  @Input()
  trainId: number;

  @Input()
  count: number;
}
