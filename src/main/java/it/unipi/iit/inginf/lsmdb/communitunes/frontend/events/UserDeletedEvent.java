package it.unipi.iit.inginf.lsmdb.communitunes.frontend.events;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.User;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.components.ReportView;
import javafx.event.Event;
import javafx.event.EventType;

public class UserDeletedEvent extends Event {
    public String username;
    public ReportView view;

    public UserDeletedEvent(EventType<? extends Event> eventType, String username, ReportView reportView) {
        super(eventType);
        this.username = username;
        view = reportView;
    }
}
