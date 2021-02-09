package it.unipi.iit.inginf.lsmdb.communitunes.frontend.events;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.ArtistPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.UserPreview;
import javafx.event.Event;
import javafx.event.EventType;

public class UserPreviewClickedEvent extends Event {
    public UserPreview preview;

    public UserPreviewClickedEvent(EventType<? extends Event> eventType, UserPreview preview) {
        super(eventType);
        this.preview = preview;
    }
}
