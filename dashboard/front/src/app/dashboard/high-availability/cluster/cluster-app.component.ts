import {Component, Input, OnInit} from "@angular/core";


@Component({
  selector: 'cluster-app',
  template: `
        <div class="cluster-app" [ngClass]="{'active': isAppActive(), 'down': down}" >    
            <div *ngIf="isAppActive()" class="cluster-app-type" [ngClass]="{'bar-app': isType('bar-app'), 'database': isType('database')}"></div>
        </div>
    `,
  styles: [require("./cluster-app.component.scss")]
})
export class ClusterAppComponent implements OnInit {
  // bar | beer
  @Input()
  name: string;

  @Input()
  down: boolean;

  ngOnInit() {
  }

  isType(type: string): boolean {
    return this.name === type;
  }

  isAppActive(): boolean {
    return !!this.name && !this.down;
  }

}
