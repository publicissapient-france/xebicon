import {NgModule} from "@angular/core";
import {CommonModule} from "@angular/common";
import {RailsComponent} from "./rails.component";
import {RailsSwitchDirective} from "./rails-moveable/rails-switch.directive";
import {TrainDisplayDirective} from "./rails-moveable/train-display.directive";
import {TrainDisplayService} from "./rails-moveable/train-display.service";
import {TrainDirective} from "./train.directive";

@NgModule({
  declarations: [
    RailsComponent,
    RailsSwitchDirective,
    TrainDisplayDirective,
    TrainDirective
  ],
  imports: [CommonModule],
  exports: [RailsComponent],
  providers: [TrainDisplayService]
})
export class RailsModule {
}
