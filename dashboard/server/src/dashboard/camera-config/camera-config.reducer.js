import Immutable from 'immutable';

const defaultUrl = '';
const initialState = Immutable.fromJS({
  cameraDepartureUrl: defaultUrl,
  cameraObstacleUrl: defaultUrl,
  cameraObstacleFlux1Url: defaultUrl,
  cameraObstacleFlux2Url: defaultUrl,
  cameraObstacleFlux3Url: defaultUrl,
  cameraArrivedUrl: defaultUrl
});


export default (state = initialState, action) => {
  switch (action.type) {
    case 'CAMERA_CONFIG':
      if (!action.payload) return state;
      return state
        .set('cameraDepartureUrl', action.payload.cameraDepartureUrl || '')
        .set('cameraObstacleUrl', action.payload.cameraObstacleUrl || '')
        .set('cameraObstacleFlux1Url', action.payload.cameraObstacleFlux1Url || '')
        .set('cameraObstacleFlux2Url', action.payload.cameraObstacleFlux2Url || '')
        .set('cameraObstacleFlux3Url', action.payload.cameraObstacleFlux3Url || '')
        .set('cameraArrivedUrl', action.payload.cameraArrivedUrl || '');
    default:
      return state;
  }
}


