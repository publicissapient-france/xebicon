import {Routes} from "@angular/router";
import {DashboardComponent} from "./dashboard/dashboard.component";
import {SimulationComponent} from "./simulation/simulation.component";
import {KitchenSinkComponent} from "./kitchensink/kitchen-sink.component";
import {NoContent} from "./no-content";

export const ROUTES: Routes = [
  {path: '', component: DashboardComponent},
  {path: 'simulation', component: SimulationComponent},
  {path: 'kitchensink', component: KitchenSinkComponent},
  {path: '**', component: NoContent},
];


