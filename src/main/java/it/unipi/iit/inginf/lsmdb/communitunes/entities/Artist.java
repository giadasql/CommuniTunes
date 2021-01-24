package it.unipi.iit.inginf.lsmdb.communitunes.entities;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.SongPreview;
import org.javatuples.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Artist extends User {

    public String StageName;

    public String Image;

    public String ActiveYears;

    public List<SongPreview> LoadedSongs = new ArrayList<>();

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
                List<Map<String, String>> loadedSongsMaps = (List<Map<String, String>>) loadedSongs;
                for (Map<String, String> songMap:
                     loadedSongsMaps) {
                    LoadedSongs.add(new SongPreview(songMap.get("songID"), this.StageName, this.Username, songMap.get("title")));
                }
            }
            catch (ClassCastException exc){
                System.out.println("exception");
                // TODO: log the exception
            }
        }
    }
}
