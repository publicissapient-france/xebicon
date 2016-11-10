import {Component} from "@angular/core";

@Component({
  selector: 'gare-icon',
  template: '',
  styles: [`
    :host {
      width: 100px;
      height: 100px;
      border-radius: 250px;
      background: grey url(../../../assets/svg/ico-gare.svg) no-repeat center;
      background-size: 64% 64%;
      display: inline-block;
    }
  `]
})
export class GareIconComponent {
}
