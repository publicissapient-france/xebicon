import {startTwitterSimulation, stopTwitterSimulation} from "./twitter-command";
import {startVoteSimulation, stopVoteSimulation} from "./vote-command";

let store;
export default (_store) => {
  store = _store;

  return {
    dispatch: (message) => {
      switch (message.type) {
        case 'START_TWITTER_SIMULATION':
          startTwitterSimulation(msg => store.dispatch(msg));
          return;
        case 'STOP_TWITTER_SIMULATION':
          stopTwitterSimulation();
          return;
        case 'START_VOTE_SIMULATION':
          startVoteSimulation(msg => store.dispatch(msg));
          return;
        case 'STOP_VOTE_SIMULATION':
          stopVoteSimulation();
          return;
      }
    }
  }
}
