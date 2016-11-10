import Immutable from 'immutable';

const initialState = Immutable.fromJS({
  imageUrl: ''
});

export default (state = initialState, action) => {
  if (!action.payload) {
    return state;
  }
  switch (action.type) {
    case 'SLIDE':
      return state
        .set('imageUrl', action.payload.imageUrl)
        .set('videoId', action.payload.videoId);
    default:
      return state;
  }
}
