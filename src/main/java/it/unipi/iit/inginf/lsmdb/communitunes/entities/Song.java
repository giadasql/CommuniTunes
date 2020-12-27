package it.unipi.iit.inginf.lsmdb.communitunes.entities;


import java.util.ArrayList;
import java.util.List;

public class Song extends Entity {

    public Artist Artist;

    public long Duration;

    public String title;

    public List<Review> LoadedReviews = new ArrayList<>();

    public List<String> Links = new ArrayList<>();

    public List<String> Genres = new ArrayList<>();

}

