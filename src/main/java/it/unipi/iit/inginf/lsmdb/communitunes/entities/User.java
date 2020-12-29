package it.unipi.iit.inginf.lsmdb.communitunes.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class User extends Entity {

    public String Email;

    public String Username;

    public Date Birthday;

    public List<Song> LoadedLikes = new ArrayList<>();

    public List<User> LoadedFollows = new ArrayList<>();

    public List<User> LoadedFollowers = new ArrayList<>();

    public User(String username, String email){
        Email = email;
        Username = username;
    }
}
