package it.unipi.iit.inginf.lsmdb.communitunes.entities;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.ArtistPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.UserPreview;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Song extends Entity {

    public ArtistPreview Artist;

    public String Duration;

    public String Title;

    public String Image;

    public String Album;

    public List<Review> LoadedReviews = new ArrayList<>();

    public List<it.unipi.iit.inginf.lsmdb.communitunes.entities.Link> Links;

    public List<String> Genres = new ArrayList<>();

    public List<ArtistPreview> Featurings = new ArrayList<>();

    public int Likes;

    public double AvgRating;

    public Song(String ID, ArtistPreview artist, String duration, String title, String image, String album, List<Review> loadedReviews, List<Link> links, List<String> genres, List<ArtistPreview> featurings, int likes, double avgRating) {
        super(ID);
        Artist = artist;
        Duration = duration;
        Title = title;
        Image = image;
        Album = album;
        LoadedReviews = loadedReviews;
        Links = links;
        Genres = genres;
        Featurings = featurings;
        Likes = likes;
        AvgRating = avgRating;
    }

//    @Override
//    public boolean equals(Object o){
//        if(o == this){
//            return true;
//        }
//        if(!(o instanceof Song)){
//            return false;
//        }
//        Song song = (Song)o;
//        Collections.sort(Genres);
//        Collections.sort(song.Genres);
//        Collections.sort(Featurings);
//        Collections.sort(song.Featurings);
//
//        return Link.equals(song.Link) && Image.equals(song.Image) &&
//                Genres.equals(song.Genres) && Featurings.equals(song.Featurings);
//    }
}

