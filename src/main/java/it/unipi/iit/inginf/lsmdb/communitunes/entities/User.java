package it.unipi.iit.inginf.lsmdb.communitunes.entities;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.ArtistPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.SongPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.UserPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.components.UserPreviewVBox;
import it.unipi.iit.inginf.lsmdb.communitunes.utilities.tools.DateToLocalDateConverter;
import org.apache.commons.codec.digest.DigestUtils;
import org.javatuples.Pair;

import java.time.LocalDate;
import java.util.*;

public class User extends Entity {

    public String Email;

    public String Username;

    public String Password;

    public String Country;

    public String Image;

    public LocalDate Birthday;

    public String FirstName;

    public String LastName;

    public List<SongPreview> LoadedLikes = new ArrayList<>();

    public List<UserPreview> LoadedFollowed = new ArrayList<>();

    public List<ArtistPreview> LoadedArtistFollowed = new ArrayList<>();

    public List<UserPreview> LoadedFollowers = new ArrayList<>();

    public List<ArtistPreview> LoadedArtistFollowers = new ArrayList<>();

    public User(String username, String email, String password, String id){
        super(id);
        Email = email;
        Username = username;
        Password =  password;
    }

        // TODO: inserire l'immagine di default
    public User(String username, String email, String password){
        super(null);
        Email = email;
        Username = username;
        Password =  password;
        Image = null;
    }

    public User(String ID, String email, String username, String password, String country, String image, LocalDate birthday, String firstName, String lastName, List<SongPreview> loadedLikes, List<UserPreview> loadedFollowed, List<ArtistPreview> loadedArtistFollowed, List<UserPreview> loadedFollowers, List<ArtistPreview> loadedArtistFollowers) {
        super(ID);
        Email = email;
        Username = username;
        Password = password;
        Country = country;
        Image = image;
        Birthday = birthday;
        FirstName = firstName;
        LastName = lastName;
        LoadedLikes = loadedLikes;
        LoadedFollowed = loadedFollowed;
        LoadedArtistFollowed = loadedArtistFollowed;
        LoadedFollowers = loadedFollowers;
        LoadedArtistFollowers = loadedArtistFollowers;
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

        return Email.equals(user.Email) && Password.equals(user.Password) && Country.equals(user.Country)
                && Birthday.equals(user.Birthday);
    }
}
