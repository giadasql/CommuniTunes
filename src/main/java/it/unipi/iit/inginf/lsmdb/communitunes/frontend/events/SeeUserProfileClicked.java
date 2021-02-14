package it.unipi.iit.inginf.lsmdb.communitunes.frontend.events;

import javafx.event.Event;
import javafx.event.EventType;

public class SeeUserProfileClicked extends Event {
    public String username;

    public SeeUserProfileClicked(EventType<? extends Event> eventType, String username) {
        super(eventType);
        this.username = username;
    }
}
