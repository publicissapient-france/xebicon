import {fromJS} from 'immutable';
import util from 'util';
const debug = require('debug')('dashboard-backend:server');

const initialState = fromJS({
  nodes: [{type: "cloud", active: true}, {type: "local", active: true}],
  shops: {"items": {"pauillac": 0, "margaux": 0, "pessac": 0}},
  buy: {"items": {"pauillac": 0, "margaux": 0, "pessac": 0}},
});

export default (state = initialState, action) => {
  switch (action.type) {
    case 'KEYNOTE_STATE':
      if (action.payload && action.payload.value === 'AVAILABILITY_START') {
        return state
          .setIn(['buy', 'items', 'pauillac'], 0).setIn(['buy', 'items', 'margaux'], 0).setIn(['buy', 'items', 'pessac'], 0)
          .setIn(['shops', 'items', 'pauillac'], 0).setIn(['shops', 'items', 'margaux'], 0).setIn(['shops', 'items', 'pessac'], 0);
      } else {
        return state;
      }
    case 'K8S_STATUS':
      if (action.payload && util.isArray(action.payload)) {
        return state.set('nodes', fromJS(action.payload));
      }
      return state;
    case 'SHOP_STATE':
      return updateShopState(state, action);
    case 'BUY':
      return updateBuy(state, action);
    default:
      return state;
  }
}

function updateBuy(state, action) {
  if (action.payload && state.getIn(['buy', 'items', action.payload.type]) !== undefined) {
    return state
      .updateIn(['buy', 'items', action.payload.type], val => val + 1);
  } else {
    debug('[WARN] Unknown BUY item: ', action.payload ? action.payload.type : '<No payload>');
    return state;
  }
}

function updateShopState(state, action) {
  if (!action.payload || !action.payload.items) {
    debug('[WARN] Message SHOP_STATE not properly formatted', JSON.stringify(action));
    return state;
  }

  const items = action.payload.items;
  return state
    .updateIn(['shops', 'items', 'pauillac'], pauillac => items.pauillac ? items.pauillac : pauillac)
    .updateIn(['shops', 'items', 'margaux'], margaux => items.margaux ? items.margaux : margaux)
    .updateIn(['shops', 'items', 'pessac'], pessac => items.pessac ? items.pessac : pessac);
}
