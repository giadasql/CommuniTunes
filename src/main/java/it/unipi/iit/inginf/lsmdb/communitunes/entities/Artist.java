package it.unipi.iit.inginf.lsmdb.communitunes.entities;

import java.util.ArrayList;
import java.util.List;

public class Artist extends User {

    List<Song> Songs;

    List<Artist> Groups = new ArrayList<>();

    public Artist(String username, String email, String password) {
        super(username, email, password);
    }
}
