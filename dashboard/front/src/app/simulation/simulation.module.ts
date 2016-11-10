import {NgModule} from "@angular/core";
import {CommonModule} from "@angular/common";
import {SimulationComponent} from "./simulation.component";
import {SimulationItemComponent} from "./simulation-item.component";
import {FormsModule} from "@angular/forms";

@NgModule({
  declarations: [
    SimulationComponent,
    SimulationItemComponent,
  ],
  imports: [CommonModule, FormsModule],
  exports: [
    SimulationComponent,
    SimulationItemComponent,
  ],
  providers: []
})
export class SimulationModule {
}
