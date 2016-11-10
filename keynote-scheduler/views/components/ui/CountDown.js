import React, {Component, PropTypes} from 'react';

import Countdown from 'react-cntdwn';

export default class CountDown extends Component {
  render() {
    const {onFinished, startAt, periodInSeconds} = this.props;

    let targetDate = new Date(startAt);
    targetDate.setSeconds(targetDate.getSeconds() + periodInSeconds);

    return (
      <span className="countdown">
        <Countdown targetDate={targetDate}
                 startDelay={0}
                 interval={1000}
                 timeSeparator={' '}
                 format={{second: 'ss'}}
                 onFinished={onFinished}/>
      </span>
    );
  }
}

CountDown.propTypes = {
  className: PropTypes.string,
  startAt: PropTypes.string.isRequired,
  periodInSeconds: PropTypes.string.isRequired,
  onFinished: PropTypes.func
};
