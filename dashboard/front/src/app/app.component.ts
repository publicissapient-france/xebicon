import {Component, ViewEncapsulation} from "@angular/core";
import {NgRedux} from "ng2-redux/lib";
import reducer from "./reducer";
import {SocketService} from "./communication/socket.service";
import {fromJS} from "immutable";

const reduxLogger = require('redux-logger');

@Component({
  selector: 'app',
  template: `
    <router-outlet></router-outlet>
  `,
  encapsulation: ViewEncapsulation.None,
  styles: [
    require('normalize.css'),
    require('./app.style.scss')
  ]
})
export class App {
  constructor(private ngRedux:NgRedux<any>, private socketService:SocketService) {
    // SocketIO
    socketService.subscribe();
    const windowWrapper:any = window;

    // Redux Store
    let enhancers = [windowWrapper.devToolsExtension ? windowWrapper.devToolsExtension() : f => f];
    this.ngRedux.configureStore(reducer, fromJS({}), [reduxLogger()], enhancers);
  }
}
