import socket from 'socket.io';
import commandHandler from "./commands/command-handler";
import {parseMessage} from "./utils/messageUtils";

export default (app) => socket.listen(app);

export const onConnection = (rabbitHandler) => (store) => (socket) => {
  socket.emit('dashboard', {type: 'SET_STATE', payload: store.getState().toJS()});

  // If vote is ongoing, we need to push to client it's remaining duration
  pushVoteDuration(store.getState(), socket);

  // Dispatch
  socket.on('dashboard', message => rabbitHandler.sendToRabbit(message));
  socket.on('internal', message => {
    const msg = parseMessage(message);
    msg && store.dispatch(msg);
  });
  const commands = commandHandler(store);
  socket.on('command', message => commands.dispatch(parseMessage(message)));
};

function pushVoteDuration(state, socket) {
  const startedAt = state.getIn(['votes', 'startedAt']);
  const voteDuration = state.getIn(['votes', 'voteDuration']);
  if (startedAt && voteDuration) {
    const finishedAt = (startedAt + voteDuration * 1000);
    const delta = Math.floor((finishedAt - (new Date().getTime())) / 1000);
    socket.emit('dashboard', {type: 'UPDATE_VOTE_DURATION', payload: {duration: delta > 0 ? delta : -1}});
  }
}
