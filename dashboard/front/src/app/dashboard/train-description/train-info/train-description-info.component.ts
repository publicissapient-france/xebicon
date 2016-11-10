import {Component, Input} from "@angular/core";

@Component({
  selector: 'train-info-desc',
  template: require('./train-description-info.component.html'),
  styles: [require('./train-description-info.component.scss')]
})
export class TrainDescriptionInfosComponent {
  @Input()
  trainId: number;

  @Input()
  count: number;
}
