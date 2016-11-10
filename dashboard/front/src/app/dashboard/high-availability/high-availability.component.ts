import {Component} from "@angular/core";
import {select} from "ng2-redux/lib/index";
import {Observable} from "rxjs/Rx";

@Component({
  selector: 'high-availability',
  template: require('./high-availability.component.html'),
  styles: [require('./high-availability.component.scss')]
})
export class HighAvailabilityComponent {
  @select(['highAvailability', 'nodes']) highAvailabilityNodes$: Observable<any>;
  @select(['highAvailability']) highAvailabilityShops$: Observable<any>;

  clusters: Array<any> = [{type: 'cloud', clusterApps:[{name:'bar'}, {name:'beer'}, {name:''}]},
                      {type: 'local', clusterApps:[{name:'bar'}, {name:'beer'}, {name:''}]}];
}
