import {Component} from "@angular/core";
import {select, NgRedux} from "ng2-redux/lib/index";
import {Observable} from "rxjs/Observable";

@Component({
  selector: 'twitter',
  template: require('./twitter.component.html'),
  styles: [
    require('./twitter.component.scss')
  ]
})
export class TwitterComponent {
  @select(['twitter', 'threads']) threads$: Observable<any>;

  threads: any = [];

  constructor() {
    this.threads$
      .filter(data => !!data)
      .subscribe(threads => {
        this.threads = threads.toJS();
      });
  }
}
