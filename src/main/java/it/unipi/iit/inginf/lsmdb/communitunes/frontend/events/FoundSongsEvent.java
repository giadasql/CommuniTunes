package it.unipi.iit.inginf.lsmdb.communitunes.frontend.events;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.ArtistPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.SongPreview;
import javafx.event.Event;
import javafx.event.EventType;

import java.util.List;

public class FoundSongsEvent extends Event {
    public List<SongPreview> foundSongs;

    public FoundSongsEvent(EventType<? extends Event> eventType, List<SongPreview> foundSongs) {
        super(eventType);
        this.foundSongs = foundSongs;
    }
}
