import {Component, Input} from "@angular/core";

@Component({
  selector: 'train',
  template: `
    <div class="train-{{trainId}}">
      <span *ngIf="trainId == 1">#ViaBdx</span>
      <span *ngIf="trainId == 2">#ViaLyon</span>
    </div>
  `,
  styles: [require('./train.component.scss')]
})
export class TrainComponent {
  @Input()
  trainId: number = 1;
}
