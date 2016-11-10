import Immutable from 'immutable';

const initialState = Immutable.fromJS({
  status: 'TWITTER'
});

export default (state = initialState, action) => {
  switch (action.type) {
    case 'KEYNOTE_STATE':
      return handleState(state, action);
    case 'SLIDE':
      return state.set('status', 'KEYNOTE_WELCOME');
    default:
      return state;
  }
}

export function handleState(state, action) {
  if (!action.payload) {
    return state;
  }
  switch (action.payload.value) {
    case 'TWITTER':
      return state.set('status', 'TWITTER');
    case 'VOTE_TRAIN_START':
      return state.set('status', 'VOTE');
    case 'VOTE_TRAIN_END':
      return state.set('status', 'VOTE_RESULT');
    case 'TRAIN_DEPARTURE_SHOW':
      return state
        .set('status', 'TRAIN_DEPARTURE')
        .set('trainId', action.payload ? action.payload.trainId : undefined);
    case 'TRAIN_DESCRIPTION_START':
      return state
        .set('status', 'TRAIN_DESCRIPTION')
        .set('trainId', action.payload ? action.payload.trainId : undefined);
    case 'HOT_DEPLOYMENT_START':
      return state.set('status', 'HOT_DEPLOYMENT');
    case 'AVAILABILITY_START':
      return state.set('status', 'HIGH_AVAILABILITY');
    case 'OBSTACLE_START':
      return state
        .set('status', 'OBSTACLE_START')
        .set('trainId', action.payload ? action.payload.trainId : undefined);
    case 'TRAIN_ARRIVED':
      return state
        .set('status', 'TRAIN_ARRIVED')
        .set('trainId', action.payload ? action.payload.trainId : undefined);
    case 'PUBLISH':
      return state.set('status', 'PUBLICATION');
    case 'CREDIT':
      return state.set('status', 'CREDIT');
    default:
      return state;
  }
}
