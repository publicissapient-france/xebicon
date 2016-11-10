import {Component, Input} from "@angular/core";

@Component({
  selector: 'element',
  template: `
        <div [ngClass]="['element', data.type]">
            <div *ngIf="data.type == 'group'">
                <h3>{{ data.name }}</h3>
                <div>
                    <element *ngFor="let element of data.elements" [data]="element" [resources]="resources"></element>
                </div>
            </div>
            <div *ngIf="data.type == 'logo' || data.type == 'picture'" [ngClass]="[data.ref, (resources[data.ref].type || '')]">
                <img *ngIf="resources[data.ref]" 
                    [src]="resources[data.ref].src" 
                    [style.width.px]="resources[data.ref].width"
                    [style.height.px]="resources[data.ref].height"/>
            </div>
            <div *ngIf="data.type == 'text' || data.type == 'note'">
                {{ data.text }}
            </div>
        </div>
    `,

  styles: [require('./element.component.scss')],
  providers: []
})
export class ElementComponent {
  @Input() data: Object;
  @Input() resources: Object;
}
