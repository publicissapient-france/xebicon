package fr.xebia.xebicon.bus;

import fr.xebia.xebicon.model.Conference;

public class SynchroFinishedEvent {

    public final boolean success;
    public final Conference conference;

    public SynchroFinishedEvent(boolean success, Conference conference) {
        this.success = success;
        this.conference = conference;
    }
}
