import { LOADING_EVENTS, LOADED_EVENTS, SENDING_EVENT, SENDED_EVENT, NEW_API_STATE, JOBS_STARTED, JOBS_STOPPED } from '../constants/ActionTypes';

const defaultState = {
  loaded: false,
  all: [],
  apiState: {
    currentState: null,
    nextState: null,
    jobs: []
  }
};

export default function events(state = defaultState, action) {
  switch (action.type) {
    case LOADING_EVENTS:
      return Object.assign({}, state, {
        loaded: false
      });
    case LOADED_EVENTS:
      return Object.assign({}, state, {
        loaded: true,
        all: action.payload.events
      });
    case NEW_API_STATE:
      return Object.assign({}, state, {
        apiState: action.payload
      });
    case JOBS_STARTED:
      return state;
    case JOBS_STOPPED:
      return state;
    default:
      return state;
  }
}
