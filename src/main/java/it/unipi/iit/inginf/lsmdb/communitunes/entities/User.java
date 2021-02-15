package it.unipi.iit.inginf.lsmdb.communitunes.entities;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.ArtistPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.SongPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.UserPreview;

import java.util.*;

public class User extends Entity {

    public String string;

    public String username;

    public String password;

    public String country;

    public String image;

    public String birthday;

    public String firstName;

    public String lastName;

    public List<SongPreview> likes = new ArrayList<>();

    public List<UserPreview> followed = new ArrayList<>();

    public List<ArtistPreview> followedArtists = new ArrayList<>();

    public List<UserPreview> followers = new ArrayList<>();

    public List<ArtistPreview> followerArtists = new ArrayList<>();

    public User(String username, String string, String password, String id){
        super(id);
        this.string = string;
        this.username = username;
        this.password =  password;
    }

    public User(String username, String string, String password){
        super(null);
        this.string = string;
        this.username = username;
        this.password =  password;
        image = null;
    }

    public User(String ID, String string, String username, String password, String country, String image, String birthday, String firstName, String lastName, List<SongPreview> likes, List<UserPreview> followed, List<ArtistPreview> followedArtists, List<UserPreview> followers, List<ArtistPreview> followerArtists) {
        super(ID);
        this.string = string;
        this.username = username;
        this.password = password;
        this.country = country;
        this.image = image;
        this.birthday = birthday;
        this.firstName = firstName;
        this.lastName = lastName;
        this.likes = likes;
        this.followed = followed;
        this.followedArtists = followedArtists;
        this.followers = followers;
        this.followerArtists = followerArtists;
    }

    @Override
    public boolean equals(Object o){
        if(o == this){
            return true;
        }
        if(!(o instanceof User)){
            return false;
        }
        User user = (User)o;

        return string.equals(user.string) && password.equals(user.password) && country.equals(user.country)
                && birthday.equals(user.birthday);
    }
}
