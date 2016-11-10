import {Component, Input, OnInit, OnChanges} from "@angular/core";


@Component({
  selector: 'clusters',
  template: `
     <cluster *ngFor="let cluster of internalClusters" [cluster]="cluster"></cluster>
  `,
  styles: [require("./clusters.component.scss")]
})
export class ClustersComponent implements OnChanges {
  @Input()
  public clusters: any;

  public internalClusters: Array<any>;

  ngOnChanges(): void {
    this.internalClusters = this.clusters.slice(0, 2);
  }
}
