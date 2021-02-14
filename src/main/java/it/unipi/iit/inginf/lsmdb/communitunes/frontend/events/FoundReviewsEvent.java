package it.unipi.iit.inginf.lsmdb.communitunes.frontend.events;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.Review;
import javafx.event.Event;
import javafx.event.EventType;

import java.util.List;

public class FoundReviewsEvent extends Event {
    public List<Review> reviewList;
    public String user;

    public FoundReviewsEvent(EventType<? extends Event> eventType, List<Review> reviews, String user) {
        super(eventType);
        reviewList = reviews;
        this.user = user;
    }
}
