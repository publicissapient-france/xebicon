import {Component, Input} from "@angular/core";

@Component({
  selector: 'progress-bar',
  template: `
    <div>
      <span>{{title}}</span>
      <div [style.width]="(done > 100 ? 100 : done) + '%'" [style.background]="color">
        <span>{{title}}</span>
      </div>
    </div>
  `,
  styles: [require('./progress-bar.component.scss')]
})
export class ProgressBarComponent {
  @Input()
  title: string = '';

  @Input()
  done: number = 0;

  @Input()
  color: string = '#6a205f';
}
