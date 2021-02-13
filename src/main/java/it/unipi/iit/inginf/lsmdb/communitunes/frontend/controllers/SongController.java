package it.unipi.iit.inginf.lsmdb.communitunes.frontend.controllers;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.*;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.ArtistPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.components.ReviewVBox;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.LayoutManager;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.LayoutManagerFactory;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.Path;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.Persistence;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.PersistenceFactory;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.neo4j.driver.internal.shaded.io.netty.util.internal.StringUtil;

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
    public VBox feat;
    public Button editSong;
    public AnchorPane writeReview;
    public Slider ratingSlider;
    public VBox reviews;
    public TextArea reviewComment;
    public HBox genres;
    private LayoutManager manager;
    private Song song;

    private boolean userLikesSong = false;
    private Persistence dbManager;

    @Override
    public void init() {
        this.manager = LayoutManagerFactory.getManager();
        dbManager = PersistenceFactory.CreatePersistence();
        song = manager.context.getFocusedSong();
        
        if(song != null){
            User authUser = manager.context.getFocusedUser();
            if(dbManager.checkLike(authUser, song)){
                likeSong.setText("Dislike");
                userLikesSong = true;
            }
            else{
                likeSong.setText("Like");
                userLikesSong = false;
            }

            if(song.Artist != null){
                Artist authArtist = manager.context.getAuthenticatedArtist();
                if(authArtist != null && (song.Artist.username != null && song.Artist.username.equals(authArtist.Username))){
                    // the song belongs to the artist watching the page
                    likeSong.setManaged(false);
                    likeSong.setVisible(false);
                    writeReview.setManaged(false);
                    writeReview.setVisible(false);
                }
                else{
                    editSong.setVisible(false);
                    editSong.setManaged(false);
                }
            }

            if(dbManager.checkIfUserReviewedSong(manager.context.getAuthenticatedUser(), song)){
                writeReview.setManaged(false);
                writeReview.setVisible(false);
            }

            if(song.Title != null){
                if(song.Title.length() >= 60){
                    songTitle.setText(song.Title.substring(0, 100) + "...");
                }
                else{
                    songTitle.setText(song.Title);
                }
            }

            if(song.Artist != null){
                artistName.setText(song.Artist.username);
                artistName.setCursor(Cursor.HAND);
                artistName.setOnMouseClicked(e -> { manager.goToArtistPage(song.Artist.username);});
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

            if(!(song.Links == null)){
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
                else{
                    links.getParent().setManaged(false);
                    links.getParent().setVisible(false);
                }
            }
            else{
                links.getParent().setManaged(false);
                links.getParent().setVisible(false);
            }

            if(!song.Featurings.isEmpty()){
                for (ArtistPreview artistPreview : song.Featurings.stream().limit(4).collect(Collectors.toList())){
                    Hyperlink hyperlink = new Hyperlink();
                    hyperlink.setText(artistPreview.username);
                    hyperlink.getStyleClass().add("artist-site");
                    hyperlink.setOnAction(e -> {
                        manager.goToArtistPage(artistPreview.username);
                    });
                    feat.getChildren().add(hyperlink);
                }
            }
            else{
                feat.getParent().setVisible(false);
                feat.getParent().setManaged(false);
            }

            if(!song.Genres.isEmpty()){
                for (String genre : song.Genres){
                    Text genreName = new Text();
                    genreName.setText(" " + genre);
                    genreName.setFill(Color.WHITE);
                    genreName.setFont(Font.font("Book Antiqua", 20));
                    genres.getChildren().add(genreName);
                }
            }
            else{
                genres.getParent().setManaged(false);
                genres.getParent().setVisible(false);
            }

            ratingSlider.valueProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(
                        ObservableValue<? extends Number> observableValue,
                        Number oldValue,
                        Number newValue) {
                    currentRating.textProperty().setValue(
                            String.valueOf(newValue.intValue()));
                }
            });


            if(song.AvgRating != -1){
                avgRating.setText(String.valueOf((int)Math.round(song.AvgRating)));
            }
            else{
                avgRating.getParent().setManaged(false);
                avgRating.getParent().setVisible(false);
            }

            for (Review review:
                 song.LoadedReviews) {
                ReviewVBox reviewBox;
                if(manager.context.getAuthenticatedUser().Username != null && review.User.equals(manager.context.getAuthenticatedUser().Username)){
                    reviewBox = new ReviewVBox(review, true);
                }
                else{
                    reviewBox = new ReviewVBox(review, false);
                }
                reviews.getChildren().add(reviewBox);
            }

            if(reviews.getChildren().isEmpty()){
                reviews.setVisible(false);
                reviews.setManaged(false);
            }
        }
    }

    public void insertReview(MouseEvent mouseEvent) {
        if(manager.context.getAuthenticatedUser() != null) {
            Review newReview;
            if(reviewComment.getText().isEmpty()){
                newReview = new Review(manager.context.getAuthenticatedUser().Username, (int)Math.round(ratingSlider.getValue()), null, song.ID);
            }
            else{
                newReview = new Review(manager.context.getAuthenticatedUser().Username, (int)Math.round(ratingSlider.getValue()), reviewComment.getText(), song.ID);
            }
            dbManager.addReview(newReview);
            manager.goToSongPage(song.ID);
        }
    }

    public void seeAllReviews(MouseEvent mouseEvent) {
        try {
            manager.setContent(Path.REVIEWS);
        } catch (IOException e) {
            // TODO: log
            e.printStackTrace();
        }
    }

    public void goToEdit(MouseEvent mouseEvent) throws IOException {
        manager.setContent(Path.SONG_EDIT);
    }


    public void likeDislikeSong(MouseEvent mouseEvent) {
        User authUser = manager.context.getAuthenticatedUser();
        if(!userLikesSong){
            dbManager.addLike(authUser, song);
            userLikesSong = true;
            likesNumber.setText(String.valueOf(song.Likes + 1));
            likeSong.setText("Dislike");
        }
        else{
            dbManager.deleteLike(authUser, song);
            userLikesSong = false;
            likeSong.setText("Like");
            likesNumber.setText(String.valueOf(song.Likes - 1));
        }
    }
}
