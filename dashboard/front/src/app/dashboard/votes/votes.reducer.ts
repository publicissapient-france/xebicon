import {fromJS} from "immutable";


const initialState: any = fromJS({
  left: createBasket(7),
  right: createBasket(7)
});

export default (state: any = initialState, action: any) => {
  switch (action.type) {
    case 'VOTE_TRAIN':
      return fillBaskets(state, action);
    case 'VOTE_ERASE':
      return eraseBaskets(state, action);
    default:
      return state;
  }
}

function fillBaskets(state, action) {
  const key = action.payload.trainId === 1 ? 'left' : 'right';
  const emptyBaskets = state.get(key)
    .filter(item => !item.get('show'));

  if (emptyBaskets.size === 0) {
    console.warn('No empty basket');
    return state;
  }

  const rIndex = Math.floor(Math.random() * emptyBaskets.size);
  const chosenIndex = emptyBaskets.get(rIndex).get('index');
  return state.updateIn([key, chosenIndex], val =>
    val
      .set('show', true)
      .set('media', action.payload.media)
      .set('avatar', action.payload.avatar)
  );
}

function eraseBaskets(state, action) {
  const key = action.payload.id === 1 ? 'left' : 'right';

  return state.updateIn([key, action.payload.index], val =>
    val
      .set('show', false)
      .set('media', '')
      .set('avatar', null)
  );
}

function createBasket(size) {
  let res = [];
  for (let i = 0; i < size; i++) {
    res.push({show: false, index: i});
  }
  return res;
}
