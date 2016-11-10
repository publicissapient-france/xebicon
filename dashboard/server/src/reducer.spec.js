import {expect} from 'chai';
import {fromJS} from 'immutable';
import reducer from './reducer';

describe('root reducer', () => {

  it('should @@init default state', () => {
    const nextState = reducer(undefined, {type: '@@INIT'});
    expect(nextState).to.have.keys(
      'keynoteState', 'highAvailability', 'obstacle', 'votes', 'hotDeployment', 'trainPosition', 'cameraConfig', 'twitter', 'slide'
    );
  });

  it('should reset store', () => {
    const nextState = reducer(fromJS({keynoteState: {status: 'VOTE'}}), {type: 'RESET_STORE'});
    expect(nextState).to.have.deep.property(['keynoteState', 'status'], 'TWITTER');
  });

});
