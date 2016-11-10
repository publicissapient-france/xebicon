import {Component} from "@angular/core";

@Component({
  selector: 'twitter-icon',
  template: '',
  styles: [`
    :host {
      width: 100px;
      height: 100px;
      border-radius: 50px;
      background: #3aaae1 url(../../../assets/svg/twitter.svg) no-repeat center;
      background-size: 59% 59%;
      display: inline-block;
    }
  `]
})
export class TwitterIconComponent {
}
