import {fromJS} from 'immutable';

const initialState = fromJS({
  lamps: [
    {id: 1, color: 0},
    {id: 2, color: 0},
    {id: 3, color: 0},
    {id: 4, color: 0},
    {id: 5, color: 0},
    {id: 6, color: 0},
    {id: 7, color: 0},
  ],
  services: [
    {id: 1, version: 'V1', status: 'STOP'},
    {id: 2, version: 'V1', status: 'STOP'},
    {id: 3, version: 'V1', status: 'STOP'},
    {id: 4, version: 'V1', status: 'STOP'},
    {id: 5, version: 'V1', status: 'STOP'},
    {id: 6, version: 'V1', status: 'STOP'},
    {id: 7, version: 'V1', status: 'STOP'},
  ]
});

export default (state = initialState, action) => {
  switch (action.type) {
    case 'LIGHT_STATE':
      return switchLight(state, action);
    case 'SERVICE_DEPLOYMENT':
      return deployService(state, action);
    default:
      return state;
  }
}

function switchLight(state, action) {
  return state.updateIn(['lamps',
      state.get('lamps').findIndex((item) => item.get("id") === action.payload.id)],
                    (item) => item.set("color", action.payload.value));
}

function deployService(state, action) {
  return state.updateIn(['services',
      state.get('services').findIndex((item) => item.get("id") === action.payload.id)],
                    (item) => item.set("version", action.payload.version)
                                  .set("status", action.payload.value === 'START' ? 'START' : 'STOP'));
}
