import {fromJS} from 'immutable';

const initialState = fromJS({
  status: undefined,
  distribution: [0, 0],
  counter: {ios: 0, android: 0, sms: 0, twitter: 0, fb: 0},
  passengers: [[], []]
});

export default (state = initialState, action) => {
  switch (action.type) {
    case 'KEYNOTE_STATE':
      return handleVoteState(state, action);
    case 'VOTE_TRAIN':
      return countVotes(state, action);
    default:
      return state;
  }
}

function countVotes(state, action) {
  if (state.get('status') !== 'VOTE') {
    return state;
  }

  const payload = action.payload;
  if (!payload.media || (payload.trainId !== 1 && payload.trainId !== 2)) {
    return state;
  }

  const media = payload.media.toLowerCase();
  if (state.getIn(['counter', media]) != undefined) {
    const increment = (payload.count || 1) > 0 ? (payload.count || 1) : 0;
    return state
      .updateIn(['counter', media], val => val + increment)
      .updateIn(['distribution', (payload.trainId === 1 ? 0 : 1)], val => val + increment)
      .updateIn(['passengers', (payload.trainId === 1 ? 0 : 1)], passengers => passengers.push(fromJS(payload)));
  }
  return state;
}

function handleVoteState(state, action) {
  if (!action.payload) {
    return state;
  }
  switch (action.payload.value) {
    case 'KEYNOTE_START':
      return initialState;
    case 'VOTE_TRAIN_START':
      return state
        .set('status', 'VOTE')
        .set('voteDuration', action.payload.voteDuration || 90)
        .set('startedAt', new Date().getTime());
    case 'VOTE_TRAIN_END':
      return state
        .set('status', 'VOTE_RESULT')
        .set('voteDuration', undefined)
        .set('startedAt', undefined);
    default:
      return state;
  }
}
