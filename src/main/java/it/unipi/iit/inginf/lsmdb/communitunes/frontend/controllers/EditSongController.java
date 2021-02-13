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

    private Song song;
    private Persistence dbManager;
    private LayoutManager manager;

    public void saveInfo(ActionEvent actionEvent) {
        if(genres.getText() == null || "".equals(genres.getText())){
            song.Genres = null;
        }
        else{
            String[] arrayGenres = genres.getText().split(";");
            song.Genres = Arrays.asList(arrayGenres);
        }
        if(link.getText() == null || "".equals(link.getText())){
            song.Links = null;
        }
        else{
            String[] arrayLinks = link.getText().split(";");
            for(String s : arrayLinks){
                String[] nameUrl = s.split(":", 2);
                if(nameUrl.length >= 2){
                    song.Links.add(new Link(nameUrl[0], nameUrl[1]));
                }
            }
        }
        if(image.getText() == null || "".equals(image.getText())){
            song.Image = null;
        }
        else{
            song.Image = image.getText();
        }
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
        StringBuilder temp = new StringBuilder();
        for(String genre : song.Genres){
            temp.append(genre).append(";");
        }
        genres.setText(temp.toString());
        temp = new StringBuilder();
        for(Link link : song.Links){
            temp.append(link.name).append(":").append(link.url).append(";");
        }
        link.setText(temp.toString());
        image.setText(song.Image);
    }

    public void deleteSong(ActionEvent actionEvent) {
        if(dbManager.deleteSong(song)){
            try {
                manager.setContent(Path.ARTIST_PROFILE);
            } catch (IOException e) {
                // TODO: log
                e.printStackTrace();
            }
        }
    }
}
