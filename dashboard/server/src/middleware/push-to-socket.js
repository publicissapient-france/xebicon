import {isVolatile} from "../utils/messageUtils";

export default (io) => (action, prevState, state, diff) => {
  if (diff && !diff.isEmpty()) {
    io.emit('dashboard', {type: 'UPDATE_STATE', payload: diff});
  }

  // Volatile Messages
  if (isVolatile(action) && state.getIn(['keynoteState', 'status']) === 'VOTE') {
    io.emit('dashboard', action);
  }
}

