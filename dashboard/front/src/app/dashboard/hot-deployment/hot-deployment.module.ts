import {NgModule} from "@angular/core";
import {CommonModule} from "@angular/common";
import {HotDeploymentComponent} from "./hot-deployment.component";
import {LampDirective} from "./lamp.directive";
import {ServiceDirective} from "./service.directive";
import {HotDeploymentService} from "./hot-deployment.service";
import {SharedModule} from "../shared/shared.module";

@NgModule({
  declarations: [
    HotDeploymentComponent,
    LampDirective,
    ServiceDirective
  ],
  imports: [CommonModule, SharedModule],
  exports: [HotDeploymentComponent],
  providers: [HotDeploymentService]
})
export class HotDeploymentModule {
}
