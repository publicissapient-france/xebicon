import {NgModule} from "@angular/core";
import {CommonModule} from "@angular/common";
import {TrainDepartureComponent} from "./train-departure.component";
import {SharedModule} from "../shared/shared.module";
import {TrainInfoComponent} from "./train-info/train-info.component";

@NgModule({
  declarations: [
    TrainDepartureComponent,
    TrainInfoComponent
  ],
  imports: [CommonModule, SharedModule],
  exports: [
    TrainDepartureComponent,
    TrainInfoComponent
  ],
  providers: []
})
export class TrainDepartureModule {
}
