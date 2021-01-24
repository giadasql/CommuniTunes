package it.unipi.iit.inginf.lsmdb.communitunes.entities.previews;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.Review;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.List;

public class SongPreview {

    public SongPreview(String ID, String artistName, String artistID, String title) {
        this.ID = ID;
        ArtistName = artistName;
        ArtistID = artistID;
        Title = title;
    }

    public String ID;

    public String ArtistName;

    public String ArtistID;

    public String Title;

}
