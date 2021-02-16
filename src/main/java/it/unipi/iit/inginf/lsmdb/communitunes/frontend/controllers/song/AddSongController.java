package it.unipi.iit.inginf.lsmdb.communitunes.frontend.controllers.song;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.Artist;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.Link;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.Song;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.ArtistPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.LayoutManager;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.LayoutManagerFactory;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.Path;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.controllers.UIController;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.Persistence;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.PersistenceFactory;
import it.unipi.iit.inginf.lsmdb.communitunes.utilities.exceptions.PersistenceInconsistencyException;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class AddSongController implements UIController {

    public Text msg;
    public TextField title;
    public TextField album;
    public TextField duration;
    public TextField genres;
    public TextField link;
    public TextField image;
    public TextField feat;

    private Song song;
    private Persistence dbManager;
    private LayoutManager manager;

    public void addSong(ActionEvent actionEvent) throws PersistenceInconsistencyException {
        if(title.getText() == null || "".equals(title.getText())){
            msg.setFill(Color.RED);
            msg.setText("The title is mandatory to add a new song.");
            return;
        }
        song = new Song(title.getText());
        Artist artist = manager.context.getFocusedArtist();
        song.artist = new ArtistPreview(artist.username, artist.image);
        if(album.getText() == null || "".equals(album.getText())){
            song.album = null;
        }
        else{
            song.album = album.getText();
        }
        if(duration.getText() == null || "".equals(duration.getText())){
            song.duration = null;
        }
        else{
            song.duration = duration.getText();
        }
        if(genres.getText() == null || "".equals(genres.getText())){
            song.genres = null;
        }
        else{
            String[] arrayGenres = genres.getText().split(";");
            song.genres = Arrays.asList(arrayGenres);
        }
        if(link.getText() == null || "".equals(link.getText())){
            song.links = null;
        }
        else{
            String[] arrayLinks = link.getText().split(";");
            song.links = new ArrayList<>();
            for(String s : arrayLinks){
                String[] nameUrl = s.split(":", 2);
                if(nameUrl.length >= 2){
                    song.links.add(new Link(nameUrl[0], nameUrl[1]));
                }
            }
        }
        if(image.getText() == null || "".equals(image.getText())){
            song.image = null;
        }
        else{
            song.image = image.getText();
        }
        if(feat.getText() == null || "".equals(feat.getText())){
            song.featList = new ArrayList<>();
        }
        else{
            String[] arrayFeat = feat.getText().split(";");
            for(String s : arrayFeat){
                song.featList.add(new ArtistPreview(s, null));
            }
        }
        if(dbManager.addSong(song)){
            msg.setFill(Color.GREEN);
            msg.setText("The information was successfully updated.");
        }
        else{
            msg.setFill(Color.RED);
            msg.setText("An error occurred while updating the information. Please try again later.");
        }
    }

    public void cancelAdd(ActionEvent actionEvent)  {
        setDefaultValues();
    }

    @Override
    public void init(){
        manager = LayoutManagerFactory.getManager();
        dbManager = manager.dbManager;
    }

    public void closeAddWindow(MouseEvent mouseEvent) throws IOException {
        manager.setContent(Path.ARTIST_PROFILE);
    }

    public void setDefaultValues(){
        title.setText("");
        album.setText("");
        duration.setText("");
        genres.setText("");
        link.setText("");
        image.setText("");
        feat.setText("");
    }
}
