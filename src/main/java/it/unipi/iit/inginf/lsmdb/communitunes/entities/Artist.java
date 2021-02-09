package it.unipi.iit.inginf.lsmdb.communitunes.entities;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.ArtistPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.SongPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.UserPreview;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Artist extends User {

    public String StageName;

    public String ActiveYears;

    public String Biography;

    public List<Link> Links;

    public List<SongPreview> LoadedSongs = new ArrayList<>();

    public Artist(String username, String email, String password) {
        super(username, email, password);
    }

    public Artist(String ID, String email, String username, String password, String country, String image, LocalDate birthday, String firstName, String lastName, List<SongPreview> loadedLikes, List<UserPreview> loadedFollowed, List<ArtistPreview> loadedArtistFollowed, List<UserPreview> loadedFollowers, List<ArtistPreview> loadedArtistFollowers, String stageName, String activeYears, String biography, List<Link> links, List<SongPreview> loadedSongs) {
        super(ID, email, username, password, country, image, birthday, firstName, lastName, loadedLikes, loadedFollowed, loadedArtistFollowed, loadedFollowers, loadedArtistFollowers);
        StageName = stageName;
        ActiveYears = activeYears;
        Biography = biography;
        Links = links;
        LoadedSongs = loadedSongs;
    }


    @Override
    public boolean equals(Object o){
        if(o == this){
            return true;
        }
        if(!(o instanceof Artist)){
            return false;
        }
        Artist artist = (Artist)o;

        return Email.equals(artist.Email) && Password.equals(artist.Password) && Country.equals(artist.Country)
                && Birthday.equals(artist.Birthday) && StageName.equals(artist.StageName) && Image.equals(artist.Image)
                && ActiveYears.equals(artist.ActiveYears) && Biography.equals(artist.Biography);
    }
}
