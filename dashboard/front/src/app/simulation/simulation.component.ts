import {Component, OnInit} from "@angular/core";
import {ConfigurationService} from "./configuration.service";
import {SimulationMessage} from "./simulation.model";
import {Observable} from "rxjs/Rx";

@Component({
  selector: 'simulation',
  styles: [`
    .title-simulation {
      margin-left: 10px;
    }
  
    .form-container {
      text-align: center;
    }
  `],
  template: `
    <h1 class="title-simulation">Simulation</h1>
    <div *ngFor="let simulation of simulationMessages|async" class="form-container">
      <simulation-item [simulation]="simulation"></simulation-item>
    </div>
  `
})
export class SimulationComponent implements OnInit {

  simulationMessages: Observable<Array<SimulationMessage>>;

  constructor(private configurationService: ConfigurationService) {
  }

  ngOnInit() {
    this.simulationMessages = this.configurationService.loadConfiguration();
  }
}
