package it.unipi.iit.inginf.lsmdb.communitunes.entities;


import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.List;

public class Song extends Entity {
    public Song(String artist, String duration, String title, String image, String album, List<Review> loadedReviews, List links, List<String> genres, List<Pair<String, String>> featurings, String id) {
        Artist = artist;
        Duration = duration;
        Title = title;
        Image = image;
        Album = album;
        LoadedReviews = loadedReviews;
        Links = links;
        Genres = genres;
        Featurings = featurings;
        ID = id;
    }

    public String Artist;

    public String Duration;

    public String Title;

    public String Image;

    public String Album;

    public List<Review> LoadedReviews = new ArrayList<>();

    public List<String> Links = new ArrayList<>();

    public List<String> Genres = new ArrayList<>();

    public List<Pair<String, String>> Featurings = new ArrayList<>();

}

