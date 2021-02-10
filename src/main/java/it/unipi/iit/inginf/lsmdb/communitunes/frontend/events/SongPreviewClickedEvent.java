package it.unipi.iit.inginf.lsmdb.communitunes.frontend.events;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.ArtistPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.SongPreview;
import javafx.event.Event;
import javafx.event.EventType;

public class SongPreviewClickedEvent extends Event {
    public SongPreview preview;

    public SongPreviewClickedEvent(EventType<? extends Event> eventType, SongPreview preview) {
        super(eventType);
        this.preview = preview;
    }
}
