import React, { Component, PropTypes } from 'react';
import moment from 'moment';

import CountDown from './ui/CountDown';

export default class Jobs extends Component {
  shouldComponentUpdate(nextProps, nextState) {
    const { events } = this.props;
    return events !== nextProps.events || events.apiState.jobs !== nextProps.events.apiState.jobs;
  }

  render() {
    const { className, events: { apiState: { jobs, currentState } } } = this.props;
    return (
      <div className={className}>
        <h1 className="title">Jobs</h1>
        <div className="jobs-list">
          <div className="container">
            <div className="timeline timeline-left keynote">
              {jobs.map((job, key) => {
                const isCurrentState = job.name === currentState.value;
                let endDate = new Date(job.next);
                endDate.setSeconds(endDate.getSeconds() + job.seconds);
                return (
                  <div className={`timeline-block ${isCurrentState ? 'current' : ''}`} key={`tl-${key}`} ref={`tl-${key}`}>
                    <div className="timeline-icon">
                      {isCurrentState && <CountDown startAt={job.next}
                                                    periodInSeconds={job.seconds}
                                                    onFinished={() => console.log('finish')} />}
                    </div>
                    <div className="timeline-content">
                      <p>{job.name}</p>
                      <div className="timeline-date">{moment(job.next).format('HH:mm:ss')}</div>
                    </div>
                  </div>
                );
              })}
            </div>
          </div>
        </div>
      </div>
    );
  }
}

Jobs.propTypes = {
  className: PropTypes.string,
  events: PropTypes.object,
  actions: PropTypes.object
};
