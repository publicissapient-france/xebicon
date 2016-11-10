import React, {Component, PropTypes} from 'react';
import {bindActionCreators} from 'redux';
import {connect} from 'react-redux';
import * as EventActions from '../actions/EventActions';
import Events from '../components/Events';
import States from '../components/States';
import Jobs from '../components/Jobs';
import Footer from '../components/Footer';
import Clock from '../components/ui/Clock';

export class App extends Component {
  render() {
    const {events, actions} = this.props;
    return (
      <div className="main-app-container">
        <header>
          <h1>Scheduler</h1>
          <div className="config">
            <div className="exchange">exchange : {events.apiState.exchangeName}</div>
            <Clock />
          </div>
        </header>
        <div className="content">
          <Events actions={actions} events={events} className="card events-list"/>
          <aside>
            <States className="card states" actions={actions} events={events}/>
            {events.apiState.jobs.length > 0 &&
            <Jobs className="card jobs" actions={actions} events={events}/>}
          </aside>
        </div>
        <Footer />
      </div>
    );
  }
}

App.propTypes = {
  actions: PropTypes.object.isRequired
};

function mapStateToProps(state) {
  return {
    events: state.events
  };
}

function mapDispatchToProps(dispatch) {
  return {
    actions: bindActionCreators(EventActions, dispatch)
  };
}

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(App);
