package it.unipi.iit.inginf.lsmdb.communitunes.entities;

import java.util.Date;

public class Review extends Entity {

    public Review(String id, String user, int rating, String text, String song) {
        super(id);
        this.user = user;
        this.rating = rating;
        this.text = text;
        this.song = song;
        timestamp = new Date();
    }

    public Review(String user, int rating, String text, String song) {
        super(null);
        this.user = user;
        this.rating = rating;
        this.text = text;
        this.song = song;
        timestamp = new Date();
    }

    public String user;

    public int rating;

    public String text;

    public String song;

    public Date timestamp;

}
