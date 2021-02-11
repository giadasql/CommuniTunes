package it.unipi.iit.inginf.lsmdb.communitunes.frontend.controllers;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.Link;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.Song;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.ArtistPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.LayoutManager;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.LayoutManagerFactory;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.Path;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.Persistence;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.PersistenceFactory;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.Arrays;

public class EditSongController implements UIController {

    public Text msg;
    public TextField genres;
    public TextField link;
    public TextField image;
    public TextField feat;

    private Song song;
    private Persistence dbManager;
    private LayoutManager manager;

    public void saveInfo(ActionEvent actionEvent) {
        if(genres.getText() == null || "".equals(genres.getText())){
            song.Genres = null;
        }
        else{
            String[] arrayGenres = genres.getText().split(" ");
            song.Genres = Arrays.asList(arrayGenres);
        }
        if(link.getText() == null || "".equals(link.getText())){
            song.Links = null;
        }
        else{
            String[] arrayLinks = link.getText().split(" ");
            for(String s : arrayLinks){
                String[] nameUrl = s.split(",");
                song.Links.add(new Link(nameUrl[0], nameUrl[1]));
            }
        }
        if(image.getText() == null || "".equals(image.getText())){
            song.Image = null;
        }
        else{
            song.Image = image.getText();
        }
        // TODO: this doesn't do what it's expected to do, maybe it's better to not allow changing the featurings
//        if(feat.getText() == null || "".equals(feat.getText())){
//            song.Featurings = null;
//        }
//        else{
//            String[] arrayFeat = feat.getText().split(" ");
//            for(String s : arrayFeat){
//                song.Featurings.add(dbManager.getArtistPreview(s));
//            }
//        }
        if(dbManager.editSong(song)){
            msg.setFill(Color.GREEN);
            msg.setText("The information was successfully updated.");
        }
        else{
            msg.setFill(Color.RED);
            msg.setText("An error occurred while updating the information. Please try again later.");
        }
    }

    public void cancelEdit(ActionEvent actionEvent) {
        setDefaultValues();
    }

    @Override
    public void init(){
        manager = LayoutManagerFactory.getManager();
        song = manager.context.getFocusedSong();
        dbManager = PersistenceFactory.CreatePersistence();
        setDefaultValues();
    }

    public void closeEditWindow(MouseEvent mouseEvent) throws IOException {
        manager.setContent(Path.SONG_PAGE);
    }

    private void setDefaultValues(){
        String temp = "";
        for(String genre : song.Genres){
            temp = temp + genre + " ";
        }
        genres.setText(temp);
        temp = "";
        for(ArtistPreview artist : song.Featurings){
            temp = temp + artist.username + " ";
        }
        feat.setText(temp);
        temp = "";
        for(Link link : song.Links){
            temp = temp + link.name + "," + link.url + " ";
        }
        link.setText(temp);
        image.setText(song.Image);
    }
}
