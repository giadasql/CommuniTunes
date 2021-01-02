package it.unipi.iit.inginf.lsmdb.communitunes.entities;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class User extends Entity {

    public String Email;

    public String Username;

    public String Password;

    public Date Birthday;

    public List<Song> LoadedLikes = new ArrayList<>();

    public List<User> LoadedFollows = new ArrayList<>();

    public List<User> LoadedFollowers = new ArrayList<>();

    public User(String username, String email, String password){
        Email = email;
        Username = username;
        try{
            MessageDigest pswdDigest = MessageDigest.getInstance("SHA-256");
            byte[] hash = pswdDigest.digest(password.getBytes(StandardCharsets.UTF_8));
            Password = Arrays.toString(hash);
        }
        catch(NoSuchAlgorithmException exc){
            Password = password;
        }
    }
}
