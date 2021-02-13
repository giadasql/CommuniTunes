package it.unipi.iit.inginf.lsmdb.communitunes.frontend.controllers;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.Artist;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.Review;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.Song;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.SongPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.components.ListHbox;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.components.ReviewVBox;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.LayoutManager;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.LayoutManagerFactory;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.Path;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.Persistence;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.PersistenceFactory;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import org.javatuples.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReviewsController implements UIController {
    public VBox reviewsBox;
    public ScrollPane scrollPane;
    public Button nextPageBtn;
    public Button prevPageBtn;
    private Song song;
    private LayoutManager manager;
    private Persistence dbManager;

    private int startIndex = 0;
    private final int count = 25;

    @Override
    public void init() {
        manager = LayoutManagerFactory.getManager();
        song = manager.context.getFocusedSong();
        dbManager = PersistenceFactory.CreatePersistence();
        showPreviews(dbManager.getReviews(song.ID, startIndex, count));
    }


    private void showPreviews(List<Review> toShow){
        scrollPane.setVvalue(0);
        reviewsBox.getChildren().clear();
        if(toShow == null){
            return;
        }
        if(toShow.size() > 0){
            for(Review review : toShow){
                if(review.User != null && manager.context.getAuthenticatedUser() != null && review.User.equals(manager.context.getAuthenticatedUser().Username)){
                    reviewsBox.getChildren().add(new ReviewVBox(review, true));
                }
                else{
                    reviewsBox.getChildren().add(new ReviewVBox(review, false));
                }
            }
        }
    }

    public void closeReviews(MouseEvent mouseEvent) {
        try {
            manager.setContent(Path.SONG_PAGE);
        } catch (IOException e) {
            // TODO: log
            e.printStackTrace();
        }
    }


    public void nextPage(ActionEvent actionEvent) {
        startIndex = startIndex + count;
        List<Review> newReviews = dbManager.getReviews(song.ID, startIndex, count);
        if(!newReviews.isEmpty()){

            showPreviews(newReviews);
            prevPageBtn.setDisable(false);
        }
        else{
            nextPageBtn.setDisable(true);
            startIndex = startIndex - count;
        }

    }

    public void prevPage(ActionEvent actionEvent) {
        if(startIndex >= count){
            startIndex = startIndex - count;
            List<Review> newReviews = dbManager.getReviews(song.ID, startIndex, count);
            showPreviews(newReviews);
            nextPageBtn.setDisable(false);
        }
        if(startIndex == 0){
            prevPageBtn.setDisable(true);
        }
    }
}