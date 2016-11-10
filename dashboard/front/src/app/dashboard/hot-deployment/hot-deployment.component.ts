import {Component, ViewChildren, QueryList, AfterViewInit} from "@angular/core";
import {LampDirective} from "./lamp.directive";
import {select} from "ng2-redux/lib/index";
import {Observable} from "rxjs/Rx";
import {ServiceDirective} from "./service.directive";

@Component({
  selector: 'hot-deployment',
  template: require('./hot-deployment.html'),
  styles: [require('./hot-deployment.component.scss')]
})
export class HotDeploymentComponent implements AfterViewInit {

  @ViewChildren(LampDirective)
  private lamps: QueryList<LampDirective>;

  @ViewChildren(ServiceDirective)
  private services: QueryList<ServiceDirective>;

  @select(['hotDeployment', 'lamps'])
  lamps$: Observable<any>;

  @select(['hotDeployment', 'services'])
  services$: Observable<any>;

  constructor() {
  }

  ngAfterViewInit() {
    this.lamps$
      .filter(lamps => !!lamps)
      .map(lamps => lamps.toJS())
      .subscribe(lamps => {
        lamps.forEach(lamp => {
          const lampDirective = this.findLamp(lamp.id);
          if (lampDirective) {
            lampDirective.changeColor(lamp.color);
          }
        });
      });

    this.services$
      .filter(services => !!services)
      .map(services => services.toJS())
      .subscribe(services => {
        services.forEach(service => {
          const serviceDirective = this.findService(service.id);
          if (serviceDirective) {
            serviceDirective.handleStatus(service.status, service.version);
          }
        });
      });
  }

  private findLamp(id: number): LampDirective {
    return this.lamps.toArray().find(lamp => {
      if (lamp.id === id) {
        return !!lamp;
      }
    });
  }

  private findService(id: number): ServiceDirective {
    return this.services.toArray().find(service => {
      if (service.id === id) {
        return !!service;
      }
    });
  }
}
