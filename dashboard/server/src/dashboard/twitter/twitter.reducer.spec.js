import {expect} from 'chai';
import {fromJS, Map, List, Range, Repeat} from 'immutable';
import reducer from './twitter.reducer';

const NBR_OF_THREADS = 3;

describe('twitter reducer', function () {

  const initialState = new Map({
    currentIndex: 0,
    threads: Range(0, NBR_OF_THREADS).map(x => List())
  });

  const tweet = { tweetId: 'fake-tweet-id' };

  it('should return initial state', () => {
    const nextState = reducer(undefined, {type: '@@INIT'});
    expect(nextState.toJS()).to.deep.equal(initialState.toJS());
  });

  Range(0, NBR_OF_THREADS).forEach((index) => {
    it(`should add tweet number ${index+1} to corresponding thread and increment currentIndex`, () => {
      // Given
      const startState = initialState
        .set('currentIndex', index)
        .set('threads', Repeat(List.of(tweet), index).concat(Repeat(List(), NBR_OF_THREADS - index)).toList());

      // When
      const nextState = reducer(startState, {type: 'TWITTER_TIMELINE', payload: tweet});

      // Then
      const expectedSate = initialState
        .set('currentIndex', (index + 1) % NBR_OF_THREADS)
        .set('threads', Repeat(List.of(tweet), index + 1).concat(Repeat(List(), NBR_OF_THREADS - 1 - index)).toList());
      expect(nextState.toJS()).to.deep.equal(expectedSate.toJS());
    });
  });

  it('should prepend fifth tweet to first thread', () => {
    const newTweet = { tweetId: 'new-fake-tweet-id' };

    // Given
    const startState = initialState
      .set('currentIndex', 0)
      .set('threads', fromJS([[tweet], [tweet], [tweet]]));

    // When
    const nextState = reducer(startState, {type: 'TWITTER_TIMELINE', payload: newTweet});

    // Then
    const expectedSate = startState
      .set('currentIndex', 1)
      .setIn(['threads', 0], fromJS([newTweet, tweet]));

    expect(nextState.toJS()).to.deep.equal(expectedSate.toJS());
  });

  it('should keep only eight tweets by thread', () => {
    // Given
    const startState = initialState
      .set('currentIndex', 0)
      .set('threads', Repeat(Repeat(fromJS(tweet), 11).toList(), 3).toList());

    // When
    const nextState = reducer(startState, {type: 'TWITTER_TIMELINE', payload: tweet});

    // Then
    const expectedSate = startState
      .set('currentIndex', 1)
      .setIn(['threads', 0], Repeat(fromJS(tweet), 8).toList());

    expect(nextState.toJS()).to.deep.equal(expectedSate.toJS());
  });
});
