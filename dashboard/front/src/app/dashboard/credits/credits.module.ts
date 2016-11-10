import {NgModule} from "@angular/core";
import {CommonModule} from "@angular/common";
import {ElevatorComponent} from "./elevator/elevator.component";
import {ElementComponent} from "./elevator/element.component";

@NgModule({
  declarations: [
    ElevatorComponent,
    ElementComponent
  ],
  imports: [
    CommonModule
  ],
  exports: [
    ElevatorComponent
  ],
  providers: []
})
export class CreditsModule {
}
