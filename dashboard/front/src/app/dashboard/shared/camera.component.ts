import {Component, Input, OnChanges} from "@angular/core";

@Component({
  selector: 'camera',
  template: `
    <img [src]="internalUrl" alt="camera"/>
  `,
  styles: [`
    :host {
        display: inline-block;
    }
    img {
        width: auto;
        height: 100%;
    }
  `]
})
export class CameraComponent implements OnChanges {
  defaultUrl: string = '../../../assets/img/train-cam.jpg';
  internalUrl: string = this.defaultUrl;

  @Input()
  url: string;

  ngOnChanges() {
    this.internalUrl = (this.url && this.url !== '') ? this.url : this.defaultUrl;
  }
}
