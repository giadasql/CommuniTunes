package it.unipi.iit.inginf.lsmdb.communitunes.frontend.events;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.ArtistPreview;
import javafx.event.Event;
import javafx.event.EventType;

public class ArtistPreviewClickedEvent extends Event {
    public ArtistPreview preview;

    public ArtistPreviewClickedEvent(EventType<? extends Event> eventType, ArtistPreview preview) {
        super(eventType);
        this.preview = preview;
    }
}
