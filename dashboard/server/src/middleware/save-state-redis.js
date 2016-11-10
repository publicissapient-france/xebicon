import {debounce} from "lodash";
import deb from "debug";
const debug = deb('dashboard-backend:server');
let redis;

function doSave(action, prevState, state, diff) {
  if (diff && !diff.isEmpty()) {
    const jsState = state.toJS();
    redis
      .setAsync('dashboardState', JSON.stringify(jsState))
      .then(() => { debug('[DEBUG] <<Save state to Redis>>'); })
      .catch(err => { debug('[WARN] Cannot write to Redis'); });
  }
}

export default (_redis) => {
  redis = _redis;
  return debounce((...params) => doSave(...params), 1000);
}
