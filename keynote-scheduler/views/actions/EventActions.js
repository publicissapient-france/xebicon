import {
    LOADING_EVENTS, LOADED_EVENTS,
    SENDING_EVENT, SENDED_EVENT, NEW_API_STATE, JOBS_STARTED, JOBS_STOPPED
} from '../constants/ActionTypes';

// fetch utils
const processStatus = function (response) {
    // status "0" to handle local files fetching (e.g. Cordova/Phonegap etc.)
    if (response.status === 200 || response.status === 0) {
        return Promise.resolve(response)
    } else {
        return Promise.reject(new Error(response.statusText))
    }
};

const parseJson = function (response) {
    return response.json();
};

export function connectToApiState() {
    return dispatch => {
        if (!!window.EventSource) {
            var source = new EventSource('/api/events/current-state');

            source.addEventListener('message', function (e) {
                console.log('SSE message !' + e.data);

                dispatch(newApiState(JSON.parse(e.data)));

                /*t.setState(

                 );*/
            }, false);

            source.addEventListener('open', function (e) {
                console.log('SSE Connected');
            }, false);

            source.addEventListener('error', function (e) {
                if (e.target.readyState == EventSource.CLOSED) {
                    console.error('SEE Disconnected');
                }
                else if (e.target.readyState == EventSource.CONNECTING) {
                    console.error('SEE Reconnecting...');
                }
            }, false)
        } else {
            console.error("Your browser doesn't support SSE");
        }
    }
}

export function startJobs() {
    return dispatch => {
        var req = new XMLHttpRequest();
        req.open('POST', '/api/events/start-jobs');
        req.onreadystatechange = function (aEvt) {
            if (req.readyState == 4) {
                if (req.status == 200) {
                    dispatch(jobsStarted());
                    console.log('success');
                } else {
                    console.error('Could not start jobs');
                }
            }
        };
        req.send();
    }
}

export function stopJobs() {
    return dispatch => {
        var req = new XMLHttpRequest();
        req.open('POST', '/api/events/stop-jobs');
        req.onreadystatechange = function (aEvt) {
            if (req.readyState == 4) {
                if (req.status == 200) {
                    dispatch(jobsStopped());
                    console.log('success');
                } else {
                    console.error('Could not start jobs');
                }
            }
        };
        req.send();
    }
}

// actions
export function loadingEvents() {
    return {
        type: LOADING_EVENTS
    };
}

export function newApiState(apiState) {
    return {
        type: NEW_API_STATE,
        payload: apiState
    };
}

export function loadedEvents(events) {
    return {
        type: LOADED_EVENTS,
        payload: {
            events
        }
    };
}

export function sendingEvent() {
    return {
        type: SENDING_EVENT
    };
}

export function sendedEvent() {
    return {
        type: SENDED_EVENT
    };
}

export function jobsStarted() {
    return {
        type: JOBS_STARTED
    };
}

export function jobsStopped() {
    return {
        type: JOBS_STOPPED
    };
}

export function send(data) {
    return dispatch => {
        dispatch(sendingEvent());

        var req = new XMLHttpRequest();
        req.open('POST', '/api/events/send');
        req.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
        req.onreadystatechange = function (aEvt) {
            if (req.readyState == 4) {
                if (req.status == 200) {
                    dispatch(sendedEvent());
                } else {
                    console.error('Could not send event');
                }
            }
        };
        req.send(JSON.stringify(data));
    };
}

export function loadAll() {
    return dispatch => {
        dispatch(loadingEvents());

        return fetch('/api/events/all')
            .then(processStatus)
            .then(parseJson)
            .then(data => dispatch(loadedEvents(data)));
    };
}
