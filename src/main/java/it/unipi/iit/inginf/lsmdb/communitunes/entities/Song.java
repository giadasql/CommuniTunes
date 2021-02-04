package it.unipi.iit.inginf.lsmdb.communitunes.entities;

import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Song extends Entity {
    public Song(String artist, String duration, String title, String image, String album, List<Review> loadedReviews, String link, List<String> likes, List<String> genres, List<Pair<String, String>> featurings, String id) {
        super(id);
        Artist = artist;
        Duration = duration;
        Title = title;
        Image = image;
        Album = album;
        LoadedReviews = loadedReviews;
        Link = link;
        Genres = genres;
        Featurings = featurings;
    }

    public Song(Object artist, Object duration, Object title, Object image, Object album, Object loadedReviews, Object link, Object likes, Object genres, Object featurings, Object id){
        super(id);
        if(artist != null){
            try{
                Artist = (String)artist;
            }
            catch (ClassCastException exc){
                // TODO: log the exception
            }
        }
        if(duration != null){
            try{
                Duration = (String)duration;
            }
            catch (ClassCastException exc){
                // TODO: log the exception
            }
        }
        if(title != null){
            try{
                Title = (String)title;
            }
            catch (ClassCastException exc){
                // TODO: log the exception
            }
        }
        if(album != null){
            try{
                Album = (String)album;
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
        if(link != null){
            try{
                Link = (String)link;
            }
            catch (ClassCastException exc){
                // TODO: log the exception
            }
        }
        if(genres != null){
            try{
                Genres.addAll((List<String>)genres);
            }
            catch (ClassCastException exc){
                // TODO: log the exception
            }
        }
        if(loadedReviews != null){
            try{
                LoadedReviews.addAll((List<Review>)loadedReviews);
            }
            catch (ClassCastException exc){
                // TODO: log the exception
            }
        }
        if(likes != null){
            try{
                LoadedLikes.addAll((List<String>)likes);
            }
            catch (ClassCastException exc){
                // TODO: log the exception
            }
        }
        if(featurings != null){
            try{
                Featurings.addAll((List<Pair<String, String>>)featurings);
            }
            catch (ClassCastException exc){
                // TODO: log the exception
            }
        }
    }

    public String Artist;

    public String Duration;

    public String Title;

    public String Image;

    public String Album;

    public List<Review> LoadedReviews = new ArrayList<>();

    public String Link;

    public List<String> Genres = new ArrayList<>();

    public List<Pair<String, String>> Featurings = new ArrayList<>();

    public List<String> LoadedLikes = new ArrayList<>();

    @Override
    public boolean equals(Object o){
        if(o == this){
            return true;
        }
        if(!(o instanceof Song)){
            return false;
        }
        Song song = (Song)o;
        Collections.sort(Genres);
        Collections.sort(song.Genres);
        Collections.sort(Featurings);
        Collections.sort(song.Featurings);

        return Link.equals(song.Link) && Image.equals(song.Image) &&
                Genres.equals(song.Genres) && Featurings.equals(song.Featurings);
    }
}

