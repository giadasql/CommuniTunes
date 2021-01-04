package it.unipi.iit.inginf.lsmdb.communitunes.entities;
import org.apache.commons.codec.digest.DigestUtils;
import java.util.ArrayList;
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
        Password =  DigestUtils.sha256Hex(password);
    }
}
