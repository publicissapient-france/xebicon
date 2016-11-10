import {fromJS, Repeat, List, Map} from 'immutable';

const NBR_OF_THREADS = 3;
const MAX_TWEET_BY_THREAD = 8;

const initialState = Map.of(
  'currentIndex', 0,
  'threads', Repeat(List(), NBR_OF_THREADS).toList()
);

export default function (state = initialState, action) {
  if (action.type === 'TWITTER_TIMELINE') {
    const currentIndex = state.get('currentIndex');
    const immutableTweet = fromJS(action.payload);

    return state
      .set('currentIndex', (currentIndex + 1) % NBR_OF_THREADS)
      .updateIn(['threads', currentIndex], thread => thread.unshift(immutableTweet).take(MAX_TWEET_BY_THREAD));
  }

  return state;
}
