package it.unipi.iit.inginf.lsmdb.communitunes.entities.previews;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.Review;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.List;

public class SongPreview implements Preview{

    public SongPreview(String ID, String artistName, String artistUsername, String title, String image) {
        this.ID = ID;
        ArtistName = artistName;
        ArtistUsername = artistUsername;
        Title = title;
        Image = image;
    }

    public SongPreview(String ID, String artistUsername, String title, String image) {
        this.ID = ID;
        ArtistUsername = artistUsername;
        Title = title;
        Image = image;
    }

    public String ID;

    public String ArtistName;

    public String ArtistUsername;

    public String Title;

    public String Image;

    @Override
    public String getIdentifier() {
        return this.ID;
    }

}
