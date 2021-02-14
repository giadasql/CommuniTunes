package it.unipi.iit.inginf.lsmdb.communitunes.frontend.events;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.Artist;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.ArtistPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.UserPreview;
import javafx.event.Event;
import javafx.event.EventType;

import java.util.List;

public class FoundArtistsEvent extends Event {
    public List<ArtistPreview> foundArtists;

    public FoundArtistsEvent(EventType<? extends Event> eventType, List<ArtistPreview> foundArtists) {
        super(eventType);
        this.foundArtists = foundArtists;
    }
}
