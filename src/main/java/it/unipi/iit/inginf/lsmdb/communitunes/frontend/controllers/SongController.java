package it.unipi.iit.inginf.lsmdb.communitunes.frontend.controllers;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.Artist;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.Link;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.Song;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.User;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.ArtistPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.LayoutManager;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.LayoutManagerFactory;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.Path;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.Persistence;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.PersistenceFactory;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.stream.Collectors;

public class SongController implements UIController {
    public Button likeSong;
    public Button modifySong;
    public Text artistName;
    public Text avgRating;
    public Text albumName;
    public VBox links;
    public Text songTitle;
    public Text currentRating;
    public Button rateButton;
    public Button seeAllReviews;
    public Text likesNumber;
    public Rectangle songImg;
    private LayoutManager manager;
    private Song song;

    @Override
    public void init() {
        this.manager = LayoutManagerFactory.getManager();
        song = manager.context.getFocusedSong();
        
        if(song != null){
            if(song.Title != null){
                songTitle.setText(song.Title);
            }

            if(song.Artist != null){
                artistName.setText(song.Artist.username);
            }
            else{
                artistName.getParent().setVisible(false);
                artistName.getParent().setManaged(false);
            }

            if(song.Image != null){
                try{
                    Image img = new Image(song.Image);
                    if (img.isError()) {
                        img = new Image(this.getClass().getResourceAsStream("/ui/img/profile-user.png"));
                    }
                    songImg.setFill(new ImagePattern(img));
                }
                catch(Exception exc) {
                    Image img = new Image(this.getClass().getResourceAsStream("/ui/img/profile-user.png"));
                    songImg.setFill(new ImagePattern(img));
                }
            }

            likesNumber.setText(String.valueOf(song.Likes));

            if(song.Album != null){
                albumName.setText(song.Album);
            }
            else{
                albumName.getParent().setVisible(false);
                albumName.getParent().setManaged(false);
            }

            if(!song.Links.isEmpty()){
                for (Link link : song.Links.stream().limit(4).collect(Collectors.toList())){
                    if(link != null && link.url != null){
                        Hyperlink hyperlink = new Hyperlink();
                        hyperlink.setText(link.name);
                        hyperlink.getStyleClass().add("song-site");
                        hyperlink.setOnAction(e -> {
                            manager.context.hostServices.showDocument(link.url);
                        });
                        links.getChildren().add(hyperlink);
                    }
                }
            }

            if(!song.Featurings.isEmpty()){
                for (ArtistPreview artistPreview : song.Featurings.stream().limit(4).collect(Collectors.toList())){
                    Hyperlink hyperlink = new Hyperlink();
                    hyperlink.setText(artistPreview.username);
                    hyperlink.getStyleClass().add("artist-site");
                    hyperlink.setOnAction(e -> {
                        manager.goToArtistPage(artistPreview.username);
                    });
                    links.getChildren().add(hyperlink);
                }
            }
        }
    }
}
