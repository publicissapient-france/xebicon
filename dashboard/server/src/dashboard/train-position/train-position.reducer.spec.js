import {expect} from 'chai';
import Immutable from 'immutable';
import reducer from './train-position.reducer';

describe('trainPosition reducer', () => {

  const initialState = Immutable.fromJS({
    trains: [
      {trainId: 1, position: '', isMoving: false},
      {trainId: 2, position: '', isMoving: false}
    ]
  });

  it('default state', () => {
    const nextState = reducer(undefined, {type: '@@INIT'});
    expect(nextState.toJS()).to.deep.equal(initialState.toJS());
  });

  describe('TRAIN_POSITION', () => {

    it('should update train 1 stationId', () => {
      const nextState = reducer(initialState, {type: 'TRAIN_POSITION', payload: {trainId: 1, stationId: 1}});
      expect(nextState.get('trains').toJS()).to.deep.equal([
        {trainId: 1, position: 'bordeaux', isMoving: false},
        {trainId: 2, position: '', isMoving: false}
      ]);
    });

    it('should update train 2 stationId', () => {
      const nextState = reducer(initialState, {type: 'TRAIN_POSITION', payload: {trainId: 2, stationId: 1}});
      expect(nextState.get('trains').toJS()).to.deep.equal([
        {trainId: 1, position: '', isMoving: false},
        {trainId: 2, position: 'bordeaux', isMoving: false}
      ]);
    });

    it('should do nothing when no payload', () => {
      const nextState = reducer(initialState, {type: 'TRAIN_POSITION'});
      expect(nextState).to.equal(initialState);
    });

    it('should do nothing when no trainId', () => {
      const nextState = reducer(initialState, {type: 'TRAIN_POSITION', payload: {}});
      expect(nextState).to.equal(initialState);
    });

    it('should do nothing when unknown trainId', () => {
      const nextState = reducer(initialState, {type: 'TRAIN_POSITION', payload: {trainId: 112, stationId: 1}});
      expect(nextState).to.equal(initialState);
    });

    it('should update stationId lowercase', () => {
      const nextState = reducer(initialState, {type: 'TRAIN_POSITION', payload: {trainId: 1, stationId: 1}});
      expect(nextState.getIn(['trains', 0, 'position'])).to.equal('bordeaux');
    });

    it('should update stationId to empty when no stationId given', () => {
      const startState = initialState.setIn(['trains', 0, 'stationId'], 'POSITION');
      const nextState = reducer(startState, {type: 'TRAIN_POSITION', payload: {trainId: 1}});
      expect(nextState.getIn(['trains', 0, 'position'])).to.equal('');
    });
  });

  describe('KEYNOTE_STATE.TRAIN_DESCRIPTION_START', () => {

    it('should erase stationId when KEYNOTE_STATE.TRAIN_DESCRIPTION_START for train 1', () => {
      const startState = initialState
        .setIn(['trains', 0, 'position'], 'POSITION_1')
        .setIn(['trains', 1, 'position'], 'POSITION_2');
      const nextState = reducer(startState, {
        type: 'KEYNOTE_STATE',
        payload: {value: 'TRAIN_DESCRIPTION_START', trainId: 1}
      });
      expect(nextState.getIn(['trains', 0, 'position'])).to.equal('start');
      expect(nextState.getIn(['trains', 1, 'position'])).to.equal('POSITION_2');
    });

    it('should erase position when KEYNOTE_STATE.TRAIN_DESCRIPTION_START for train 2', () => {
      const startState = initialState
        .setIn(['trains', 0, 'position'], 'POSITION_1')
        .setIn(['trains', 1, 'position'], 'POSITION_2');
      const nextState = reducer(startState, {
        type: 'KEYNOTE_STATE',
        payload: {value: 'TRAIN_DESCRIPTION_START', trainId: 2}
      });
      expect(nextState.getIn(['trains', 0, 'position'])).to.equal('POSITION_1');
      expect(nextState.getIn(['trains', 1, 'position'])).to.equal('start');
    });

    it('should do nothing when no train id', () => {
      const startState = initialState
        .setIn(['trains', 0, 'position'], 'POSITION_1')
        .setIn(['trains', 1, 'position'], 'POSITION_2');
      const nextState = reducer(startState, {type: 'KEYNOTE_STATE', payload: {value: 'TRAIN_DESCRIPTION_START'}});
      expect(nextState).to.equal(startState);
    });

    it('should do nothing when unknown train id', () =>  {
      const startState = initialState
        .setIn(['trains', 0, 'position'], 'POSITION_1')
        .setIn(['trains', 1, 'position'], 'POSITION_2');
      const nextState = reducer(startState, {type: 'KEYNOTE_STATE', payload: {value: 'TRAIN_DESCRIPTION_START', trainId: 112}});
      expect(nextState).to.equal(startState);
    });
  });

  describe('KEYNOTE_STATE.TRAIN_DEPARTURE_START', () => {

    it('should move train when KEYNOTE_STATE.TRAIN_DEPARTURE_START for train 1', () => {
      const nextState = reducer(initialState, {
        type: 'KEYNOTE_STATE',
        payload: {value: 'TRAIN_DEPARTURE_START', trainId: 1}
      });
      expect(nextState.get('trains').toJS()).to.deep.equal([
        {trainId: 1, position: '', isMoving: true},
        {trainId: 2, position: '', isMoving: false}
      ]);
    });

    it('should stop train when KEYNOTE_STATE.TRAIN_POSITION for train 1', () => {

      const startState = initialState
        .setIn(['trains', 0, 'position'], 'lyon')
        .setIn(['trains', 0, 'isMoving'], true)
        .setIn(['trains', 1, 'position'], 'bordeaux')
        .setIn(['trains', 1, 'isMoving'], true);

      const nextState = reducer(startState, {
        type: 'TRAIN_POSITION',
        payload: {trainId: 1, stationId: 3}
      });
      expect(nextState.get('trains').toJS()).to.deep.equal([
        {trainId: 1, position: 'xebicon', isMoving: false},
        {trainId: 2, position: 'bordeaux', isMoving: true}
      ]);
    });
  });

});
