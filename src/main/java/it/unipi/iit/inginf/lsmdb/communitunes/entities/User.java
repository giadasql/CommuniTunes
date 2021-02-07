package it.unipi.iit.inginf.lsmdb.communitunes.entities;
import it.unipi.iit.inginf.lsmdb.communitunes.utilities.tools.DateToLocalDateConverter;
import org.apache.commons.codec.digest.DigestUtils;
import org.javatuples.Pair;

import java.time.LocalDate;
import java.util.*;

public class User extends Entity {
    public User(Object email, Object username, Object password, Object image, Object firstName, Object lastName, Object country, Object birthday, Object loadedLikes,Object loadedFollowed, Object loadedArtistFollowed, Object loadedFollowers, Object loadedArtistFollowers, Object id) {
        super(id);
        if(email != null){
            try{
                Email = (String)email;
            }
            catch (ClassCastException exc){
                // TODO: log the exception
            }
        }
        if(username != null){
            try{
                Username = (String)username;
            }
            catch (ClassCastException exc){
                // TODO: log the exception
            }
        }
        if(password != null){
            try{
                Password = (String)password;
            }
            catch (ClassCastException exc){
                // TODO: log the exception
            }
        }
        if(image != null){
            try{
                Image = (String)image;
            }
            catch (ClassCastException exc){
                // TODO: log the exception
            }
        }
        if(firstName != null){
            try{
                FirstName = (String)firstName;
            }
            catch (ClassCastException exc){
                // TODO: log the exception
            }
        }
        if(lastName != null){
            try{
                LastName = (String)lastName;
            }
            catch (ClassCastException exc){
                // TODO: log the exception
            }
        }
        if(country != null){
            try{
                Country = (String)country;
            }
            catch (ClassCastException exc){
                // TODO: log the exception
            }
        }
        if(birthday != null){
            try{
                Birthday = DateToLocalDateConverter.convertToLocalDateViaInstant((Date)birthday);
            }
            catch (ClassCastException exc){
                // TODO: log the exception
            }
        }
        if(loadedLikes != null){
            try{
                LoadedLikes = (List<Pair<String, String>>) loadedLikes;
            }
            catch (ClassCastException exc){
                // TODO: log the exception
            }
        }
        if(loadedArtistFollowed != null){
            try{
                LoadedArtistFollowed = (List<String>) loadedArtistFollowed;
            }
            catch (ClassCastException exc){
                // TODO: log the exception
            }
        }
        if(loadedArtistFollowers != null){
            try{
                LoadedArtistFollowers = (List<String>) loadedArtistFollowers;
            }
            catch (ClassCastException exc){
                // TODO: log the exception
            }
        }
        if(loadedFollowed != null){
            try{
                LoadedFollowed = (List<String>) loadedFollowed;
            }
            catch (ClassCastException exc){
                // TODO: log the exception
            }
        }
        if(loadedFollowers != null){
            try{
                LoadedFollowers = (List<String>) loadedFollowers;
            }
            catch (ClassCastException exc){
                // TODO: log the exception
            }
        }
    }

    public String Email;

    public String Username;

    public String Password;

    public String Country;

    public String Image;

    public LocalDate Birthday;

    public String FirstName;

    public String LastName;

    public List<Pair<String, String>> LoadedLikes = new ArrayList<>();

    public List<String> LoadedFollowed = new ArrayList<>();

    public List<String> LoadedArtistFollowed = new ArrayList<>();

    public List<String> LoadedFollowers = new ArrayList<>();

    public List<String> LoadedArtistFollowers = new ArrayList<>();

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
