package it.unipi.iit.inginf.lsmdb.communitunes.entities;

import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Artist extends User {

    public String StageName;

    public String Image;

    public String ActiveYears;

    public List<Map<String, String>> LoadedSongs;

    public Artist(String username, String email, String password) {
        super(username, email, password);
    }

    public Artist(Object email, Object username, Object password, Object country, Object birthday, Object loadedLikes, Object loadedFollowed, Object loadedArtistFollowed, Object loadedFollowers, Object loadedArtistFollowers, Object stageName, Object image, Object activeYears, Object loadedSongs, Object id) {
        super(email, username, password, country, birthday, loadedLikes, loadedFollowed, loadedArtistFollowed, loadedFollowers, loadedArtistFollowers, id);
        if(stageName != null){
            try{
                StageName = (String)stageName;
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
        if(activeYears != null){
            try{
                ActiveYears = (String) activeYears;
            }
            catch (ClassCastException exc){
                // TODO: log the exception
            }
        }
        if(loadedSongs != null){
            try{
                LoadedSongs = (List<Map<String, String>>) loadedSongs;
            }
            catch (ClassCastException exc){
                // TODO: log the exception
            }
        }
    }
}
