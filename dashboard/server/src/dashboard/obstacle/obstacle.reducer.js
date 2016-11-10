import Immutable from 'immutable';

const initialState = Immutable.fromJS({
  obstacleType: '',
  isDisplayed: false
});


export default (state = initialState, action) => {
  switch (action.type) {
    case 'OBSTACLE':
      return state
        .set('obstacleType', action.payload.obstacleType)
        .set('isDisplayed', true);
    case 'OBSTACLE_CLEARED':
      return clearObstacle(state);
    case 'KEYNOTE_STATE':
      return (action.payload && action.payload.value === 'OBSTACLE_START') ? clearObstacle(state) : state;
    default:
      return state;
  }
}

function clearObstacle(state) {
  return state
    .set('obstacleType', '')
    .set('isDisplayed', false);
}
