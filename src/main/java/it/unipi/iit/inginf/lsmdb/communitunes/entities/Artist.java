package it.unipi.iit.inginf.lsmdb.communitunes.entities;

import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.List;

public class Artist extends User {

    public String StageName;

    // <SongTitle, SongID>
    public List<Pair<String, String>> LoadedSongs;

    public Artist(String username, String email, String password) {
        super(username, email, password);
    }
}
