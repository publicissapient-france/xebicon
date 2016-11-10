import {createStore, applyMiddleware} from "redux";
import reducer from "./reducer";
import {fromJS} from "immutable";
import dashboardMiddleware from "./middleware/dashboard-middleware";
import logger from "./middleware/redux-logger";
import saveState from "./middleware/save-state-redis";
import pushToSocket from "./middleware/push-to-socket";

export default function makeStore(io, initState, redis) {
  const mw = dashboardMiddleware(logger, saveState(redis), pushToSocket(io));
  return createStore(reducer, fromJS(initState), applyMiddleware(mw));
}
