import {Component, Input, OnChanges} from "@angular/core";

@Component({
  selector: 'train-message',
  template: `
    <div class="content">
      <div class="train-{{trainId}}">
        <train-icon></train-icon>
        <train [trainId]="trainId"></train>
        <span class="space">{{internalMessage}}</span>
        <img  class="space" *ngIf="showXebiConImg" src="../../../assets/svg/xebicon.svg" alt="XebiCon2016"/>
        <station class="space" *ngIf="stationId" [stationId]="stationId"></station>
      </div>
      <span *ngIf="way" class="way">{{way}}</span>
    </div>
  `,
  styles: [require('./train-message.component.scss')]
})
export class TrainMessageComponent implements OnChanges {
  showXebiConImg: boolean = false;
  trainId: number = 1;
  stationId: number = 0;
  internalMessage: string = '';
  way: string = '';

  @Input()
  message: string = '';


  ngOnChanges(c) {
    this.trainId = this.message.indexOf('<Train1>') !== -1 ? 1 : 2;
    this.showXebiConImg = this.message.indexOf('<XebiConImg>') !== -1;
    if (this.message.indexOf('<Voie') !== -1) {
      this.way = this.message.indexOf('<Voie1>') !== -1 ? 'Voie 1' : 'Voie 2';
    }
    if (this.message.indexOf('<Station') !== -1) {
      this.stationId = this.message.indexOf('<Station1>') !== -1 ? 1 : 2;
    } else {
      this.stationId = 0;
    }

    this.internalMessage = this.message.replace(/<[\w\d]*>/g, '');
  }

}
