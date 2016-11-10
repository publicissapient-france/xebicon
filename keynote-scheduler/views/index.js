import React from 'react';
import ReactDOM from 'react-dom';

import './styles/main.scss';

import {configureStore} from './store/configureStore';
import {Root} from './containers/Root';

const store = configureStore();

import {connectToApiState} from './actions/EventActions';

store.dispatch(connectToApiState()); // connect to /api/events/current-state

ReactDOM.render(
    <Root store={store}/>,
    document.getElementById('root')
);
