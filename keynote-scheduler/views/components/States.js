import React, {Component, PropTypes} from 'react';

import {MODES} from '../../config/constants';

import ToggleButton from 'react-toggle-button';

export default class States extends Component {
  render() {
    const {className, events: {apiState: {currentState, nextState, mode}}, actions} = this.props;
    return (
      <div className={className}>
        <h1 className="title">States</h1>
        <div className="state-list">
          <ul>
            <li className="mode-switch">
              Automatic
              <ToggleButton
                className="toggle-switch"
                value={mode === MODES.AUTOMATIC}
                onToggle={(value) => {
                  if(value) {
                    actions.stopJobs();
                  } else {
                    actions.startJobs();
                  }
                }}/>
            </li>
            <li>Current state :
              <div>{JSON.stringify(currentState)}</div>
            </li>
            <li>Next state :
              <div>{JSON.stringify(nextState)}</div>
            </li>
          </ul>
        </div>
      </div>
    );
  }
}

States.propTypes = {
  className: PropTypes.string,
  events: PropTypes.object,
  actions: PropTypes.object
};
