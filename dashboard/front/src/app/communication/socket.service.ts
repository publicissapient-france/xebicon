import * as io from "socket.io-client";
import {Injectable} from "@angular/core";
import {NgRedux} from "ng2-redux/lib/index";

@Injectable()
export class SocketService {
  socket;

  constructor(private ngRedux: NgRedux<any>) {
    let uri = location.protocol + '//' + location.hostname;

    if (ENV === 'production' && location.port) {
      uri += ':8086'; // + location.port;
    }
    if (ENV === 'development') {
      uri += ':8001';
    }

    //noinspection TypeScriptUnresolvedFunction
    this.socket = io.connect(uri);
  }

  subscribe() {
    //noinspection TypeScriptUnresolvedFunction
    this.socket.on('dashboard', (message:any) => {
      if (message.type) {
        this.ngRedux.dispatch(message);
      } else {
        console.warn('Unknown message type', message);
      }
    });
  }

  pushToServer(data: any, isInternal: boolean = false) {
    //noinspection TypeScriptUnresolvedFunction
    this.socket.emit(isInternal ? 'internal' : 'dashboard', data);
  }
  commandToServer(data: any) {
    //noinspection TypeScriptUnresolvedFunction
    this.socket.emit('command', data);
  }
}
