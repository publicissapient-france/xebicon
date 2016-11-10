
export default function logger(action, prevState, state, diff) {
  // TODO: Logger
  console.log('ACTION : ', action);
  console.log('  DIFF : ', diff ? diff : '<THE SAME>');
}
