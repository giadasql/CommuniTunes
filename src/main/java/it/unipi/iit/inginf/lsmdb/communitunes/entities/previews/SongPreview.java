package it.unipi.iit.inginf.lsmdb.communitunes.entities.previews;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.Review;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.List;

public class SongPreview {

    public SongPreview(String ID, String artistName, String artistUsername, String title) {
        this.ID = ID;
        ArtistName = artistName;
        ArtistUsername = artistUsername;
        Title = title;
    }

    public String ID;

    public String ArtistName;

    public String ArtistUsername;

    public String Title;

}
