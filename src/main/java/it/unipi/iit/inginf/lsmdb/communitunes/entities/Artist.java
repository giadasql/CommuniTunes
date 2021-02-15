package it.unipi.iit.inginf.lsmdb.communitunes.entities;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.ArtistPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.SongPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.UserPreview;

import java.util.ArrayList;
import java.util.List;

public class Artist extends User {

    public String stageName;

    public String activeYears;

    public String biography;

    public List<Link> links;

    public List<SongPreview> songPreviews = new ArrayList<>();

    public Artist(String username, String email, String password) {
        super(username, email, password);
    }

    public Artist(String ID, String email, String username, String password, String country, String image, String birthday, String firstName, String lastName, List<SongPreview> loadedLikes, List<UserPreview> loadedFollowed, List<ArtistPreview> loadedArtistFollowed, List<UserPreview> loadedFollowers, List<ArtistPreview> loadedArtistFollowers, String stageName, String activeYears, String biography, List<Link> links, List<SongPreview> songPreviews) {
        super(ID, email, username, password, country, image, birthday, firstName, lastName, loadedLikes, loadedFollowed, loadedArtistFollowed, loadedFollowers, loadedArtistFollowers);
        this.stageName = stageName;
        this.activeYears = activeYears;
        this.biography = biography;
        this.links = links;
        this.songPreviews = songPreviews;
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

        return string.equals(artist.string) && password.equals(artist.password) && country.equals(artist.country)
                && birthday.equals(artist.birthday) && stageName.equals(artist.stageName) && image.equals(artist.image)
                && activeYears.equals(artist.activeYears) && biography.equals(artist.biography);
    }
}
