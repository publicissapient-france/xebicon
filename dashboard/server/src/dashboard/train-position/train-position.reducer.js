import Immutable from 'immutable';

const initialState = Immutable.fromJS({
  trains: [
    {trainId: 1, position: '', isMoving: false},
    {trainId: 2, position: '', isMoving: false}
  ]
});

const getPosition = (position) => {
  switch (position)  {
    case 1:
      return 'bordeaux';
    case 2:
      return 'lyon';
    case 3:
    case 4:
      return 'xebicon';
    default:
      return '';
  }
};

export default (state = initialState, action) => {
  switch (action.type) {
    case 'KEYNOTE_STATE':
      return erasePosition(state, action);
    case 'TRAIN_POSITION':
      return trainPosition(state, action);
    default:
      return state;
  }
}

const extracted = (state, action, trainPosition) => {
  return state.updateIn(['trains',
      state.get('trains').findIndex((item) => item.get("trainId") === action.payload.trainId)],
    (item) => item.set('position', trainPosition ? trainPosition.toLowerCase() : '')
      .set('isMoving', false));
};

function trainPosition(state, action) {
  const payload = action.payload;
  if (payload && (payload.trainId === 1 || payload.trainId === 2)) {
    return extracted(state, action, getPosition(payload.stationId));
  }
  return state;
}


function erasePosition(state, action) {
  const payload = action.payload;

  if (payload && payload.value === 'KEYNOTE_START') {
    return initialState;
  }

  if (payload && (payload.trainId === 1 || payload.trainId === 2)) {
    if (payload.value === 'TRAIN_DESCRIPTION_START') {
      return extracted(state, action, 'start');
    }
    if (payload.value === 'OBSTACLE_START') {
      return extracted(state, action, 'obstacle');
    }
    if (payload.value === 'TRAIN_DEPARTURE_START') {
      return state.setIn(['trains', (payload.trainId === 1 ? 0 : 1), 'isMoving'], true);
    }
  }
  return state;
}
