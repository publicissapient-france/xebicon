import {NgModule} from "@angular/core";
import {CommonModule} from "@angular/common";
import {HighAvailabilityComponent} from "./high-availability.component";
import {UtilsModule} from "../../utils/utils.module";
import {ShopBillComponent} from "./shop/shop-bill.component";
import {SharedModule} from "../shared/shared.module";
import {ClusterComponent} from "./cluster/cluster.component";
import {ClusterAppComponent} from "./cluster/cluster-app.component";
import {ClustersComponent} from "./cluster/clusters.component";

@NgModule({
  declarations: [
    HighAvailabilityComponent,
    ShopBillComponent,
    ClusterComponent,
    ClusterAppComponent,
    ClustersComponent
  ],
  imports: [CommonModule, UtilsModule, SharedModule],
  exports: [
    HighAvailabilityComponent,
    ShopBillComponent,
    ClustersComponent,
  ],
  providers: []
})
export class HighAvailabilityModule {
}
