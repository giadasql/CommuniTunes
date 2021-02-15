package it.unipi.iit.inginf.lsmdb.communitunes.entities;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.ArtistPreview;

import java.util.ArrayList;
import java.util.List;

public class Song extends Entity {

    public ArtistPreview artist;

    public String duration;

    public String title;

    public String image;

    public String album;

    public List<Review> reviews = new ArrayList<>();

    public List<it.unipi.iit.inginf.lsmdb.communitunes.entities.Link> links;

    public List<String> genres = new ArrayList<>();

    public List<ArtistPreview> featList = new ArrayList<>();

    public int likes;

    public double avgRating;

    public Song(String title){
        super(null);
        this.title = title;
    }

    public Song(String ID, ArtistPreview artist, String duration, String title, String image, String album, List<Review> reviews, List<Link> links, List<String> genres, List<ArtistPreview> featList, int likes, double avgRating) {
        super(ID);
        this.artist = artist;
        this.duration = duration;
        this.title = title;
        this.image = image;
        this.album = album;
        this.reviews = reviews;
        this.links = links;
        this.genres = genres;
        this.featList = featList;
        this.likes = likes;
        this.avgRating = avgRating;
    }
}

