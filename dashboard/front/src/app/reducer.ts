import votesVolatile from "./dashboard/votes/votes.reducer";
import * as Redux from "redux-immutable";
import {fromJS} from "immutable";
import * as patch from "immutablepatch";

const {combineReducers} = Redux;

const combinedReducer = combineReducers({
  votesVolatile
});
// keynoteState: ((state=fromJS({})) => state)

export default (state, action) => {
  switch (action.type) {
    case 'SET_STATE':
      return state.merge(action.payload);
    case 'UPDATE_STATE':
      return patch(state, fromJS(action.payload));
    case 'UPDATE_VOTE_DURATION':
      return state.setIn(['votes', 'voteDuration'], action.payload.duration);
    case '@@INIT':
    case 'VOTE_TRAIN':
    case 'VOTE_ERASE':
      return state.set('volatile', combinedReducer(state.get('volatile'), action));
    default:
      return state;
  }
};
