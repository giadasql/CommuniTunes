package it.unipi.iit.inginf.lsmdb.communitunes.frontend.events;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.Report;
import javafx.event.Event;
import javafx.event.EventType;

public class ShowUserReviewsEvent extends Event {
    public Report report;

    public ShowUserReviewsEvent(EventType<? extends Event> eventType, Report report) {
        super(eventType);
        this.report = report;
    }
}
