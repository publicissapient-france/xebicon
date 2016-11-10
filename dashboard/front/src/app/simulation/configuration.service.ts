import {Injectable} from "@angular/core";
import {Http} from "@angular/http";
import {Observable} from "rxjs/Rx";
import {SimulationMessage} from "./simulation.model";

@Injectable()
export class ConfigurationService {

  private configFile:string = 'assets/messages.json';

  constructor(private http:Http) {
  }

  loadConfiguration():Observable<Array<SimulationMessage>> {
    return this.http.get(this.configFile)
      .map(res => res.json())
      .flatMap(res => Observable.from(res))
      .map((simulation:SimulationMessage) => {
        simulation.json = JSON.stringify(simulation.message, null, 4);
        return simulation
      })
      .reduce((acc, current) => [...acc, current], []);
  }
}
