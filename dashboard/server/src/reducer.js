import {Map} from 'immutable';
import {combineReducers} from 'redux-immutable';
import keynoteState from './dashboard/keynote.reducer';
import highAvailability from './dashboard/high-availability/high-availability.reducer';
import obstacle from './dashboard/obstacle/obstacle.reducer';
import votes from './dashboard/votes/votes.reducer';
import hotDeployment from './dashboard/hot-deployment/hot-deployment.reducer';
import trainPosition from './dashboard/train-position/train-position.reducer';
import cameraConfig from './dashboard/camera-config/camera-config.reducer';
import twitter from './dashboard/twitter/twitter.reducer'
import slide from './dashboard/slide/slide.reducer'

const appReducer = combineReducers({
  keynoteState, highAvailability, obstacle, votes, hotDeployment, trainPosition, cameraConfig, twitter, slide
});

export default (state, action) => {
  if (action.type === 'RESET_STORE') {
    state = new Map();
  }
  return appReducer(state, action)
};
