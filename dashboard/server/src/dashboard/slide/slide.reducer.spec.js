import {expect} from 'chai';
import Immutable from 'immutable';
import reducer from './slide.reducer';

describe('slide reducer', () => {

  it('default state', () => {
    const nextState = reducer(undefined, {type: 'UNKNOWN'});
    expect(nextState.toJS()).to.deep.equal({imageUrl: ''});
  });

  it('state not change when unknown action', () => {
    const state = Immutable.fromJS({imageUrl: ''});
    const nextState = reducer(state, {type: 'UNKNOWN'});
    expect(nextState).to.equal(state);
  });

  it('state change when SLIDE message with image url', () => {
    const state = Immutable.fromJS({imageUrl: ''});
    const nextState = reducer(state, {type: 'SLIDE', payload: {imageUrl: 'test_url'}});
    expect(nextState.toJS()).to.deep.equal({imageUrl: 'test_url', videoId: undefined});
  });

  it('state do nothing when no payload', () => {
    const state = Immutable.fromJS({imageUrl: 'test_url1'});
    const nextState = reducer(state, {type: 'SLIDE'});
    expect(nextState).to.equal(state);
  });

});
