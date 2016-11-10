import {expect} from 'chai';
import {fromJS} from 'immutable';
import reducer from './keynote.reducer';

describe('keynote reducer', () => {

  it('default state', () => {
    const nextState = reducer(undefined, {type: 'UNKNOWN'});
    expect(nextState.toJS()).to.deep.equal({status: 'TWITTER'});
  });

  it('state not change when unknown action', () => {
    const state = fromJS({status: 'KEYNOTE_WELCOME'});
    const nextState = reducer(state, {type: 'UNKNOWN', payload: {value: 'VOTE_STATION_START'}});
    expect(nextState).to.equal(state);
  });

  it('application state: VOTE_TRAIN_START', () => {
    const state = fromJS({status: 'KEYNOTE_WELCOME'});
    const nextState = reducer(state, {type: 'KEYNOTE_STATE', payload: {value: 'VOTE_TRAIN_START'}});
    expect(nextState.toJS()).to.deep.equal({"status": "VOTE"});
  });

  it('application state: VOTE_TRAIN_END', () => {
    const state = fromJS({status: 'VOTE'});
    const nextState = reducer(state, {type: 'KEYNOTE_STATE', payload: {value: 'VOTE_TRAIN_END'}});
    expect(nextState.toJS()).to.deep.equal({"status": "VOTE_RESULT"});
  });

  it('application state: KEYNOTE_WELCOME at SLIDE MESSAGE', () => {
    const state = fromJS({status: 'VOTE'});
    const nextState = reducer(state, {type: 'SLIDE', payload: {}});
    expect(nextState.toJS()).to.deep.equal({"status": "KEYNOTE_WELCOME"});
  });

  it('application state: TWITTER', () => {
    const state = fromJS({status: 'VOTE'});
    const nextState = reducer(state, {type: 'KEYNOTE_STATE', payload: {value: 'TWITTER'}});
    expect(nextState.toJS()).to.deep.equal({"status": "TWITTER"});
  });

  it('application state: HOT_DEPLOYMENT_START', () => {
    const state = fromJS({status: 'VOTE'});
    const nextState = reducer(state, {type: 'KEYNOTE_STATE', payload: {value: 'HOT_DEPLOYMENT_START'}});
    expect(nextState.toJS()).to.deep.equal({"status": "HOT_DEPLOYMENT"});
  });

  it('application state: TRAIN_DEPARTURE_SHOW', () => {
    const state = fromJS({status: 'VOTE'});
    const nextState = reducer(state, {type: 'KEYNOTE_STATE', payload: {value: 'TRAIN_DEPARTURE_SHOW', trainId: 1}});
    expect(nextState.toJS()).to.deep.equal({"status": "TRAIN_DEPARTURE", trainId: 1});
  });

  it('application state: PUBLISH', () => {
    const state = fromJS({status: 'VOTE'});
    const nextState = reducer(state, {type: 'KEYNOTE_STATE', payload: {value: 'PUBLISH', trainId: 1}});
    expect(nextState.toJS()).to.deep.equal({"status": "PUBLICATION"});
  });
});
