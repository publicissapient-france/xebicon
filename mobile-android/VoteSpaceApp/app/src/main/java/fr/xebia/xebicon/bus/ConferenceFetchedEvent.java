package fr.xebia.xebicon.bus;

public class ConferenceFetchedEvent {

    public final boolean success;

    public ConferenceFetchedEvent(boolean success) {
        this.success = success;
    }
}
