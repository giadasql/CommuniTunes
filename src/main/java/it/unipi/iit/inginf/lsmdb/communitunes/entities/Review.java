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

    public Review(Object id, Object user, Object rating, Object text, Object song) {
        super(id);
        if(user != null){
            try{
                this.user = (String)user;
            }
            catch (ClassCastException exc){
                // TODO: log the exception
            }
        }
        if(rating != null){
            try{
                this.rating = (Integer) rating;
            }
            catch (ClassCastException exc){
                // TODO: log the exception
            }
        }
        if(text != null){
            try{
                this.text = (String)text;
            }
            catch (ClassCastException exc){
                // TODO: log the exception
            }
        }
        if(song != null){
            try{
                this.song = (String)song;
            }
            catch (ClassCastException exc){
                // TODO: log the exception
            }
        }
        timestamp = new Date();
    }

    public String user;

    public int rating;

    public String text;

    public String song;

    public Date timestamp;

}
