import React, {Component, PropTypes} from 'react';
import _ from 'lodash';

import ImgTrain from '../assets/train.png';
import ImgTrainGreen from '../assets/train-green.png';

class EventButton extends Component {
  render() {
    const {event, isCurrentState, isNextState, action} = this.props;

    const label = `${event.payload.value} ${event.payload.trainId ? '/ TRAIN: ' + event.payload.trainId : ''} ${event.payload.stationId ? '/ STATION: ' + event.payload.stationId : ''}`;
    const showTrain = event.type === 'TRAIN_POSITION';
    return (
      <div onClick={() => action(event)}
              className={`event-container ${isCurrentState ? 'current' : (isNextState ? 'next' : '')}`}>
        <div className={`event-tile-container ${isCurrentState ? 'current' : (isNextState ? 'next' : '')}`}>
          <div className="event-tile event-tile-visible">
            {showTrain && <img src={ImgTrain} alt="train"/>}
            <span>{label}</span>
          </div>
          <div className="event-tile event-tile-hidden">
            {showTrain && <img src={ImgTrainGreen} alt="train"/>}
            <h4>{label}</h4>
          </div>
        </div>
      </div>
    );
  }
}

export default class Events extends Component {
  constructor(props, context) {
    super(props, context);
  }

  componentDidMount() {
    this.props.actions.loadAll();
  }

  render() {
    const {events, className, actions} = this.props;
    return (
      <div className={className}>
        <h1 className="title">Events</h1>
        {!events.loaded && <span>Loading...</span>}
        {events.loaded && <ul>
          {Object.keys(events.all).map((val, key) => {
            const event = events.all[val];
            const isCurrentState = _.isEqual(event.payload, events.apiState.currentState);
            const isNextState = _.isEqual(event.payload, events.apiState.nextState);
            return (
              <li key={key}>
                <EventButton
                  event={event}
                  isCurrentState={isCurrentState}
                  isNextState={isNextState}
                  action={actions.send}/>
              </li>
            );
          })}
        </ul>}
      </div>
    );
  }
}

Events.propTypes = {
  events: PropTypes.object.isRequired,
  actions: PropTypes.object.isRequired,
  className: PropTypes.string
};
