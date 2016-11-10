import {NgModule} from "@angular/core";
import {CommonModule} from "@angular/common";
import {SharedModule} from "../shared/shared.module";
import {TrainInfoComponent} from "./train-info/train-info.component";
import {TrainArrivedComponent} from "./train-arrived.component";

@NgModule({
  declarations: [
    TrainArrivedComponent,
  ],
  imports: [CommonModule, SharedModule],
  exports: [
    TrainArrivedComponent,
  ],
  providers: []
})
export class TrainArrivedModule {
}
