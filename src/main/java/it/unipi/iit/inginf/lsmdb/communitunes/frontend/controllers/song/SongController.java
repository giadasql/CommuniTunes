package it.unipi.iit.inginf.lsmdb.communitunes.frontend.controllers.song;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.*;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.ArtistPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.Main;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.components.ReviewVBox;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.LayoutManager;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.LayoutManagerFactory;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.Path;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.controllers.UIController;
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
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        dbManager = manager.dbManager;
        song = manager.context.getFocusedSong();
        
        if(song != null){
            if(!manager.context.inAdminPanel) {
                User authUser = manager.context.getAuthenticatedUser();
                if (dbManager.checkLike(authUser, song)) {
                    likeSong.setText("Dislike");
                    userLikesSong = true;
                } else {
                    likeSong.setText("Like");
                    userLikesSong = false;
                }
            }

            if(song.artist != null){
                if(!manager.context.inAdminPanel){
                    Artist authArtist = manager.context.getAuthenticatedArtist();
                    if(authArtist != null && (song.artist.username != null && song.artist.username.equals(authArtist.username))){
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
                    if(dbManager.checkIfUserReviewedSong(manager.context.getAuthenticatedUser(), song)){
                        writeReview.setManaged(false);
                        writeReview.setVisible(false);
                    }
                }
                else{
                    likeSong.setVisible(false);
                    editSong.setVisible(false);
                    writeReview.setVisible(false);
                }
            }
            if(song.title != null){
                if(song.title.length() >= 60){
                    songTitle.setText(song.title.substring(0, 100) + "...");
                }
                else{
                    songTitle.setText(song.title);
                }
            }

            if(song.artist != null){
                artistName.setText(song.artist.username);
                artistName.setCursor(Cursor.HAND);
                artistName.setOnMouseClicked(e -> { manager.goToArtistPage(song.artist.username);});
            }
            else{
                artistName.getParent().setVisible(false);
                artistName.getParent().setManaged(false);
            }

            if(song.image != null){
                try{
                    Image img = new Image(song.image);
                    if (img.isError()) {
                        img = new Image(this.getClass().getResourceAsStream("/ui/img/song_default.png"));
                    }
                    songImg.setFill(new ImagePattern(img));
                }
                catch(Exception exc) {
                    Image img = new Image(this.getClass().getResourceAsStream("/ui/img/song_default.png"));
                    songImg.setFill(new ImagePattern(img));
                }
            }
            else{
                Image img = new Image(this.getClass().getResourceAsStream("/ui/img/song_default.png"));
                songImg.setFill(new ImagePattern(img));
            }

            likesNumber.setText(String.valueOf(song.likes));

            if(song.album != null){
                albumName.setText(song.album);
            }
            else{
                albumName.getParent().setVisible(false);
                albumName.getParent().setManaged(false);
            }

            if(!(song.links == null)){
                if(!song.links.isEmpty()){
                    for (Link link : song.links.stream().limit(4).collect(Collectors.toList())){
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

            if(!song.featList.isEmpty()){
                for (ArtistPreview artistPreview : song.featList.stream().limit(4).collect(Collectors.toList())){
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

            if(!song.genres.isEmpty()){
                for (String genre : song.genres){
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


            if(song.avgRating != -1){
                avgRating.setText(String.valueOf((int)Math.round(song.avgRating)));
            }
            else{
                avgRating.getParent().setManaged(false);
                avgRating.getParent().setVisible(false);
            }

            for (Review review:
                 song.reviews) {
                ReviewVBox reviewBox;
                if(!manager.context.inAdminPanel){
                    if(manager.context.getAuthenticatedUser().username != null && review.user.equals(manager.context.getAuthenticatedUser().username)){
                        reviewBox = new ReviewVBox(review, true, false);
                    }
                    else{
                        reviewBox = new ReviewVBox(review, false, true);
                    }
                }
                else{
                    reviewBox = new ReviewVBox(review, false, false);
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
                newReview = new Review(manager.context.getAuthenticatedUser().username, (int)Math.round(ratingSlider.getValue()), null, song.id);
            }
            else{
                newReview = new Review(manager.context.getAuthenticatedUser().username, (int)Math.round(ratingSlider.getValue()), reviewComment.getText(), song.id);
            }
            dbManager.addReview(newReview);
            manager.goToSongPage(song.id);
        }
    }

    public void seeAllReviews(MouseEvent mouseEvent) {
        try {
            manager.setContent(Path.REVIEWS);
        } catch (IOException e) {
            Logger logger = LoggerFactory.getLogger(SongController.class);
            logger.error("An exception occurred: ", e);
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
            likesNumber.setText(String.valueOf(song.likes + 1));
            likeSong.setText("Dislike");
        }
        else{
            dbManager.deleteLike(authUser, song);
            userLikesSong = false;
            likeSong.setText("Like");
            likesNumber.setText(String.valueOf(song.likes - 1));
        }
    }
}
