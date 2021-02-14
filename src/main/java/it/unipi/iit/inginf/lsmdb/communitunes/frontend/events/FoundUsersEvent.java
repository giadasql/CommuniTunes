package it.unipi.iit.inginf.lsmdb.communitunes.frontend.events;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.UserPreview;
import javafx.event.Event;
import javafx.event.EventType;

import java.util.List;

public class FoundUsersEvent extends Event {
    public final List<UserPreview> foundUsers;

    public FoundUsersEvent(EventType<? extends Event> eventType, List<UserPreview> foundUsers) {
        super(eventType);
        this.foundUsers = foundUsers;
    }
}
