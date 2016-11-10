import {expect} from 'chai';
import Immutable, {Map, List} from 'immutable';
import reducer from './votes.reducer';

describe('votes reducer', () => {

  it('should return default state', () => {
    const nextState = reducer(undefined, {type: 'UNKNOWN'});
    expect(nextState.get('distribution').toJS()).to.deep.equal([0, 0]);
  });

  it('should update vote status to VOTE', () => {
    const state = Immutable.fromJS({status: undefined});
    const nextState = reducer(state, {type: 'KEYNOTE_STATE', payload: {value: 'VOTE_TRAIN_START'}});
    expect(nextState.get('status')).to.equal('VOTE');
    expect(nextState.get('voteDuration')).to.equal(90);
    expect(nextState.get('startedAt')).not.to.be.undefined;
  });

  it('should reset vote sate on KEYNOTE_STATE.KEYNOTE_START', () => {
    const state = Immutable.fromJS({status: 'VOTE', distribution: [10, 8], counter: {ios: 1, android: 9, sms: 8, twitter: 3, fb: 4}, passengers: [[{},{}], []]});
    const nextState = reducer(state, {type: 'KEYNOTE_STATE', payload: {value: 'KEYNOTE_START'}});
    expect(nextState.getIn(['distribution', 0])).to.equal(0);
    expect(nextState.getIn(['distribution', 1])).to.equal(0);
    expect(nextState.getIn(['counter', 'ios'])).to.equal(0);
    expect(nextState.getIn(['passengers', 0]).size).to.equal(0);
  });

  it('should update vote status to VOTE_RESULT', () => {
    const state = Immutable.fromJS({status: 'VOTE'});
    const nextState = reducer(state, {type: 'KEYNOTE_STATE', payload: {value: 'VOTE_TRAIN_END'}});
    expect(nextState.get('status')).to.equal('VOTE_RESULT');
    expect(nextState.get('voteDuration')).to.be.undefined;
    expect(nextState.get('startedAt')).to.be.undefined;
  });

  it('should not update vote status when status not concerned votes', () => {
    const state = Immutable.fromJS({status: 'VOTE'});
    const nextState = reducer(state, {type: 'KEYNOTE_STATE', payload: {value: 'TWITTER'}});
    expect(nextState.get('status')).to.equal('VOTE');
  });


  it('should do nothing when status is not VOTE_STATION', () => {
    const state = Immutable.fromJS({status: undefined, counter: {ios: 0, android: 0, sms: 0, twitter: 0}, distribution: [0, 0], passengers: [[], []]});
    const nextState = reducer(state, {type: 'VOTE_STATION', payload: {media: 'ios', trainId: 1}});
    expect(nextState.get('counter').toJS()).to.deep.equal({ios: 0, android: 0, sms: 0, twitter: 0});
  });

  it('should increment counter for ios +1', () => {
    const state = Immutable.fromJS({status: 'VOTE', counter: {ios: 0, android: 0, sms: 0, twitter: 0}, distribution: [0, 0], passengers: [[], []]});
    const nextState = reducer(state, {type: 'VOTE_TRAIN', payload: {media: 'ios', trainId: 1}});
    expect(nextState.get('counter')).to.deep.equal(Map({ios: 1, android: 0, sms: 0, twitter: 0}));
  });

  it('should increment counter for ios +count', () => {
    const state = Immutable.fromJS({status: 'VOTE', counter: {ios: 1, android: 0, sms: 0, twitter: 0}, distribution: [0, 0], passengers: [[], []]});
    const nextState = reducer(state, {type: 'VOTE_TRAIN', payload: {media: 'ios', count: 3, trainId: 1}});
    expect(nextState.get('counter')).to.deep.equal(Map({ios: 4, android: 0, sms: 0, twitter: 0}));
  });

  it('should increment counter for android +1', () => {
    const state = Immutable.fromJS({status: 'VOTE', counter: {ios: 0, android: 0, sms: 0, twitter: 0}, distribution: [0, 0], passengers: [[], []]});
    const nextState = reducer(state, {type: 'VOTE_TRAIN', payload: {media: 'android', trainId: 1}});
    expect(nextState.get('counter')).to.deep.equal(Map({ios: 0, android: 1, sms: 0, twitter: 0}));
  });

  it('should increment counter for android +count', () => {
    const state = Immutable.fromJS({status: 'VOTE', counter: {ios: 1, android: 10, sms: 0, twitter: 0}, distribution: [0, 0], passengers: [[], []]});
    const nextState = reducer(state, {type: 'VOTE_TRAIN', payload: {media: 'android', count: 3, trainId: 1}});
    expect(nextState.get('counter')).to.deep.equal(Map({ios: 1, android: 13, sms: 0, twitter: 0}));
  });

  it('should increment counter for sms +count', () => {
    const state = Immutable.fromJS({status: 'VOTE', counter: {ios: 1, android: 0, sms: 8, twitter: 0}, distribution: [0, 0], passengers: [[], []]});
    const nextState = reducer(state, {type: 'VOTE_TRAIN', payload: {media: 'sms', count: 2, trainId: 1}});
    expect(nextState.get('counter')).to.deep.equal(Map({ios: 1, android: 0, sms: 10, twitter: 0}));
  });

  it('should do nothing when unknown media', () => {
    const state = Immutable.fromJS({status: 'VOTE', counter: {ios: 1, android: 0, sms: 8, twitter: 0}, distribution: [0, 0], passengers: [[], []]});
    const nextState = reducer(state, {type: 'VOTE_TRAIN', payload: {media: 'unknown', count: 2, trainId: 1}});
    expect(nextState.get('counter')).to.deep.equal(Map({ios: 1, android: 0, sms: 8, twitter: 0}));
  });

  it('should do nothing when unknown trainId', () => {
    const state = Immutable.fromJS({status: 'VOTE', counter: {ios: 1, android: 0, sms: 8, twitter: 0}, distribution: [0, 0], passengers: [[], []]});
    const nextState = reducer(state, {type: 'VOTE_TRAIN', payload: {media: 'sms', count: 2, trainId: 10}});
    expect(nextState).to.equal(state);
  });

  it('should increment distribution item N0', () => {
    const state = Immutable.fromJS({status: 'VOTE', counter: {ios: 1, android: 0, sms: 0, twitter: 0}, distribution: [2, 3], passengers: [[], []] });
    const nextState = reducer(state, {type: 'VOTE_TRAIN', payload: {trainId: 1, media: 'ios', count: 3}});
    expect(nextState.get('distribution')).to.deep.equal(List.of(5, 3));
  });

  it('should increment distribution item N1', () => {
    const state = Immutable.fromJS({status: 'VOTE', counter: {ios: 1, android: 0, sms: 0, twitter: 0}, distribution: [2, 3], passengers: [[], []] });
    const nextState = reducer(state, {type: 'VOTE_TRAIN', payload: {trainId: 2, media: 'ios', count: 3}});
    expect(nextState.get('distribution')).to.deep.equal(List.of(2, 6));
  });

  it('should add passenger to trainId 1', () => {
    const state = Immutable.fromJS({status: 'VOTE', counter: {ios: 0, android: 0, sms: 0, twitter: 0}, distribution: [0, 0], passengers: [[], []]});
    const nextState = reducer(state, {type: 'VOTE_TRAIN', payload: {media: 'ios', trainId: 1}});
    expect(nextState.getIn(['passengers', 1]).size).to.equal(0);
    expect(nextState.getIn(['passengers', 0]).size).to.equal(1);
    expect(nextState.getIn(['passengers', 0, 0]).toJS()).to.deep.equal({ "media": "ios", "trainId": 1 });
  });

  it('should add passenger to trainId 2', () => {
    const state = Immutable.fromJS({status: 'VOTE', counter: {ios: 0, android: 0, sms: 0, twitter: 0}, distribution: [0, 0], passengers: [[], []]});
    const nextState = reducer(state, {type: 'VOTE_TRAIN', payload: {media: 'ios', trainId: 2}});
    expect(nextState.getIn(['passengers', 0]).size).to.equal(0);
    expect(nextState.getIn(['passengers', 1]).size).to.equal(1);
    expect(nextState.getIn(['passengers', 1, 0]).toJS()).to.deep.equal({ "media": "ios", "trainId": 2 });
  });
});
