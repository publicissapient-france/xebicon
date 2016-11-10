import {NgModule} from "@angular/core";
import {CommonModule} from "@angular/common";
import {TrainIconComponent} from "./train-icon.component";
import {TwitterIconComponent} from "./twitter-icon.component";
import {TrainMessageComponent} from "./train-message.component";
import {TrainComponent} from "./train.component";
import {StationComponent} from "./station.component";
import {StationIconComponent} from "./station-icon.component";
import {StationMessageComponent} from "./station-message.component";
import {CameraComponent} from "./camera.component";
import {HeaderComponent} from "./header/header.component";
import {ProgressBarComponent} from "./progress-bar.component";

@NgModule({
  declarations: [
    TwitterIconComponent,
    TrainIconComponent,
    TwitterIconComponent,
    TrainComponent,
    StationIconComponent,
    StationComponent,
    TrainMessageComponent,
    StationMessageComponent,
    CameraComponent,
    HeaderComponent,
    ProgressBarComponent
  ],
  imports: [
    CommonModule
  ],
  exports: [
    TwitterIconComponent,
    TrainIconComponent,
    TrainComponent,
    StationIconComponent,
    StationComponent,
    TrainMessageComponent,
    StationMessageComponent,
    CameraComponent,
    HeaderComponent,
    ProgressBarComponent
  ],
  providers: []
})
export class SharedModule {
}
