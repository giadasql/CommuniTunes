package it.unipi.iit.inginf.lsmdb.communitunes.entities;

public class Review extends Entity {

    public Review(String user, int rating, String text, String song) {
        User = user;
        Rating = rating;
        Text = text;
        Song = song;
    }

    public String User;

    public int Rating;

    public String Text;

    public String Song;

}
