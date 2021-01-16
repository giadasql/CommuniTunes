package it.unipi.iit.inginf.lsmdb.communitunes.entities;
import org.apache.commons.codec.digest.DigestUtils;
import org.javatuples.Pair;

import java.util.*;

public class User extends Entity {

    public String Email;

    public String Username;

    public String Password;

    public Date Birthday;

    public List<Pair<String, String>> LoadedLikes = new ArrayList<>();

    public List<String> LoadedFollows = new ArrayList<>();

    public List<String> LoadedFollowers = new ArrayList<>();

    public User(String username, String email, String password){
        Email = email;
        Username = username;
        Password =  password;
    }
}
