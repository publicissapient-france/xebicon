import {NgModule} from "@angular/core";
import {CommonModule} from "@angular/common";
import {SharedModule} from "../shared/shared.module";
import {TrainDescriptionComponent} from "./train-description.component.ts";
import {TrainDescriptionInfosComponent} from "./train-info/train-description-info.component.ts";
import {PassengerItemComponent} from "./passanger/passenger-item.component.ts";
import {PassengerListComponent} from "./passanger/passenger-list.component.ts";

@NgModule({
  declarations: [
    TrainDescriptionComponent,
    TrainDescriptionInfosComponent,
    PassengerItemComponent,
    PassengerListComponent
  ],
  imports: [CommonModule, SharedModule],
  exports: [
    TrainDescriptionComponent,
    TrainDescriptionInfosComponent,
    PassengerItemComponent,
    PassengerListComponent
  ],
  providers: []
})
export class TrainDescriptionModule {
}
