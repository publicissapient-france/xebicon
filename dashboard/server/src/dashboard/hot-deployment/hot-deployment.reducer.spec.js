import {expect} from 'chai';
import {fromJS} from 'immutable';
import reducer from './hot-deployment.reducer';

describe('hot deployment reducer', () => {

  const initialState = fromJS({
    lamps: [
      {id: 1, color: 1},
      {id: 2, color: 0.6},
      {id: 3, color: 0.2},
    ],
    services: [
      {id: 1, version: 'V1', status: 'STOP'},
      {id: 2, version: 'V1', status: 'STOP'},
      {id: 3, version: 'V1', status: 'STOP'},
    ]
  });

  it('should return default state', () => {
    const nextState = reducer(undefined, {type: 'UNKNOWN'});
    expect(nextState.get('lamps').toJS()).to.deep.equal([
      {id: 1, color: 0},
      {id: 2, color: 0},
      {id: 3, color: 0},
      {id: 4, color: 0},
      {id: 5, color: 0},
      {id: 6, color: 0},
      {id: 7, color: 0},
    ]);

    expect(nextState.get('services').toJS()).to.deep.equal([
      {id: 1, version: 'V1', status: 'STOP'},
      {id: 2, version: 'V1', status: 'STOP'},
      {id: 3, version: 'V1', status: 'STOP'},
      {id: 4, version: 'V1', status: 'STOP'},
      {id: 5, version: 'V1', status: 'STOP'},
      {id: 6, version: 'V1', status: 'STOP'},
      {id: 7, version: 'V1', status: 'STOP'},
    ]);
  });

  it('should update lamps luminosity', () => {
    const nextStateLamps2 = reducer(initialState, {type: "LIGHT_STATE", payload: {id: 2, value: 1}});
    const nextStateLamps3 = reducer(nextStateLamps2, {type: "LIGHT_STATE", payload: {id: 3, value: 0.0}});

    expect(nextStateLamps2.get('lamps').toJS()).to.deep.equal([
      {id: 1, color: 1},
      {id: 2, color: 1},
      {id: 3, color: 0.2}]);

    expect(nextStateLamps3.get('lamps').toJS()).to.deep.equal([
      {id: 1, color: 1},
      {id: 2, color: 1},
      {id: 3, color: 0.0}]);
  });

  it('should update service version', () => {
    const nextStateService1 = reducer(initialState, {type: "SERVICE_DEPLOYMENT", payload: {value: 'START', id: 1, version: 'V2'}});
    const nextStateService3 = reducer(nextStateService1, {type: "SERVICE_DEPLOYMENT", payload: {value: 'START', id: 3, version: 'V2'}});
    const nextStateStopService3 = reducer(nextStateService3, {type: "SERVICE_DEPLOYMENT", payload: {value: 'END', id: 3, version: 'V2'}});
    const nextStateStopService1 = reducer(nextStateStopService3, {type: "SERVICE_DEPLOYMENT", payload: {value: 'END', id: 1, version: 'V2'}});
    const nextStateService2 = reducer(nextStateStopService1, {type: "SERVICE_DEPLOYMENT", payload: {value: 'END', id: 2, version: 'V2'}});

    expect(nextStateService1.get('services').toJS()).to.deep.equal([
      {id: 1, version: 'V2', status: 'START'},
      {id: 2, version: 'V1', status: 'STOP'},
      {id: 3, version: 'V1', status: 'STOP'}]);

    expect(nextStateService3.get('services').toJS()).to.deep.equal([
      {id: 1, version: 'V2', status: 'START'},
      {id: 2, version: 'V1', status: 'STOP'},
      {id: 3, version: 'V2', status: 'START'}]);

    expect(nextStateStopService3.get('services').toJS()).to.deep.equal([
      {id: 1, version: 'V2', status: 'START'},
      {id: 2, version: 'V1', status: 'STOP'},
      {id: 3, version: 'V2', status: 'STOP'}]);

    expect(nextStateStopService1.get('services').toJS()).to.deep.equal([
      {id: 1, version: 'V2', status: 'STOP'},
      {id: 2, version: 'V1', status: 'STOP'},
      {id: 3, version: 'V2', status: 'STOP'}]);

    expect(nextStateService2.get('services').toJS()).to.deep.equal([
      {id: 1, version: 'V2', status: 'STOP'},
      {id: 2, version: 'V2', status: 'STOP'},
      {id: 3, version: 'V2', status: 'STOP'}]);
  });
});
