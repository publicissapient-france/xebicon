import {Component, Input} from "@angular/core";

@Component({
  selector: 'publish-message',
  template: `
    <img [src]="icon" alt=""/>
    <span [style.color]="color">{{ title }}</span>
  `,
  styles: [require('./publish-message.component.scss')]
})
export class PublishMessageComponent {
  @Input()
  title: string = '';

  @Input()
  icon: string = '../../../../assets/svg/github.svg';

  @Input()
  color: string = '#6a205f';
}
