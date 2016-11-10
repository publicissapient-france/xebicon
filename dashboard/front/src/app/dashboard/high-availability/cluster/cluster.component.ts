import {Component, Input, OnInit, OnChanges} from "@angular/core";


@Component({
  selector: 'cluster',
  template: `
    <div class="cluster" [ngClass]="{'down': !cluster.active}">
       <cluster-app *ngFor="let clusterApp of cluster.apps.slice(0, maxApp)" 
            [name]="clusterApp.name" [down]="!cluster.active" >
       </cluster-app>
       <div class="container {{cluster.type}}"  [ngClass]="{'down': !cluster.active}"></div>
    </div>
`,
  styles: [require("./cluster.component.scss")]
})
export class ClusterComponent implements OnChanges {
  maxApp: number = 3;

  @Input()
  public cluster: any;


  ngOnChanges(): void {
    if (!this.cluster) {
      return;
    }

    if (this.cluster) {
      if (!this.cluster.apps) {
        this.cluster.apps = [];
      }
    }
    if (this.cluster.apps.length < this.maxApp) {
      this.cluster.apps.push({name: ''});
      this.ngOnChanges();
    }
  }

}
