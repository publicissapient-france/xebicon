import {expect} from 'chai';
import Immutable from 'immutable';
import reducer from './camera-config.reducer';

describe('camera config reducer', () => {

  it('default state', () => {
    const nextState = reducer(undefined, {type: 'UNKNOWN'});
    expect(nextState.toJS().cameraDepartureUrl).not.to.be.undefined;
    expect(nextState.toJS().cameraObstacleUrl).not.to.be.undefined;
    expect(nextState.toJS().cameraObstacleFlux1Url).not.to.be.undefined;
    expect(nextState.toJS().cameraObstacleFlux2Url).not.to.be.undefined;
    expect(nextState.toJS().cameraObstacleFlux3Url).not.to.be.undefined;
  });

  it('should not fail even when no payload', () => {
    const state = Immutable.fromJS({cameraDepartureUrl: '', cameraObstacleUrl: '', cameraObstacleFlux1Url: '', cameraObstacleFlux2Url: '', cameraObstacleFlux3Url: '', cameraArrivedUrl: ''});
    const nextState = reducer(state, {type: 'CAMERA_CONFIG'});
    expect(nextState.toJS()).to.deep.equal({cameraDepartureUrl: '', cameraObstacleUrl: '', cameraObstacleFlux1Url: '', cameraObstacleFlux2Url: '', cameraObstacleFlux3Url: '', cameraArrivedUrl: ''});
  });

  it('should not fail even when payload not good', () => {
    const state = Immutable.fromJS({cameraDepartureUrl: '', cameraObstacleUrl: '', cameraObstacleFlux1Url: '', cameraObstacleFlux2Url: '', cameraObstacleFlux3Url: '', cameraArrivedUrl: ''});
    const nextState = reducer(state, {type: 'CAMERA_CONFIG', payload: []});
    expect(nextState.toJS()).to.deep.equal({cameraDepartureUrl: '', cameraObstacleUrl: '', cameraObstacleFlux1Url: '', cameraObstacleFlux2Url: '', cameraObstacleFlux3Url: '', cameraArrivedUrl: ''});
  });

  it('should not fail even when payload is empty', () => {
    const state = Immutable.fromJS({cameraDepartureUrl: '', cameraObstacleUrl: '', cameraObstacleFlux1Url: '', cameraObstacleFlux2Url: '', cameraObstacleFlux3Url: '', cameraArrivedUrl: ''});
    const nextState = reducer(state, {type: 'CAMERA_CONFIG', payload: {}});
    expect(nextState.toJS()).to.deep.equal({cameraDepartureUrl: '', cameraObstacleUrl: '', cameraObstacleFlux1Url: '', cameraObstacleFlux2Url: '', cameraObstacleFlux3Url: '', cameraArrivedUrl: ''});
  });

  it('should update one camera url', () => {
    const state = Immutable.fromJS({cameraDepartureUrl: '', cameraObstacleUrl: '', cameraObstacleFlux1Url: '', cameraObstacleFlux2Url: '', cameraObstacleFlux3Url: ''});
    const nextState = reducer(state, {type: 'CAMERA_CONFIG', payload: {cameraDepartureUrl: 'url'}});
    expect(nextState.toJS()).to.deep.equal({cameraDepartureUrl: 'url', cameraObstacleUrl: '', cameraObstacleFlux1Url: '', cameraObstacleFlux2Url: '', cameraObstacleFlux3Url: '', cameraArrivedUrl: ''});
  });

});
