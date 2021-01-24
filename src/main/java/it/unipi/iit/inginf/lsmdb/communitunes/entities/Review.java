package it.unipi.iit.inginf.lsmdb.communitunes.entities;

public class Review extends Entity {

    public Review(String id, String user, int rating, String text, String song) {
        super(id);
        User = user;
        Rating = rating;
        Text = text;
        Song = song;
    }

    public Review(String user, int rating, String text, String song) {
        super(null);
        User = user;
        Rating = rating;
        Text = text;
        Song = song;
    }

    public Review(Object id, Object user, Object rating, Object text, Object song) {
        super(id);
        if(user != null){
            try{
                User = (String)user;
            }
            catch (ClassCastException exc){
                // TODO: log the exception
            }
        }
        if(rating != null){
            try{
                Rating = (Integer) rating;
            }
            catch (ClassCastException exc){
                // TODO: log the exception
            }
        }
        if(text != null){
            try{
                Text = (String)text;
            }
            catch (ClassCastException exc){
                // TODO: log the exception
            }
        }
        if(song != null){
            try{
                Song = (String)song;
            }
            catch (ClassCastException exc){
                // TODO: log the exception
            }
        }
    }

    public String User;

    public int Rating;

    public String Text;

    public String Song;

}
