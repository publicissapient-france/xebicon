import immutableDiff from 'immutablediff';

export default function dashboardMiddleware(...dashboardMiddlewares) {
  return ({getState}) => next => action => {
    const prevState = getState();
    let returnValue = next(action);
    const state = getState();

    const diff = (prevState !== state) ? immutableDiff(prevState, state) : undefined;
    dashboardMiddlewares.forEach((fn) => fn(action, prevState, state, diff));

    return returnValue;
  };
};
