package it.unipi.iit.inginf.lsmdb.communitunes.frontend.components;

import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.LayoutManagerFactory;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.Persistence;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.PersistenceFactory;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class AdminReviewView extends VBox {
    private String reviewID;
    private String song;
    private String user;

    public AdminReviewView(String user, String id, String text, String song) {
        reviewID = id;
        this.song = song;
        this.user = user;
        Line line = new Line();
        line.setStartX(0);
        line.setStartY(0);
        line.setEndX(150);
        line.setEndY(0);
        line.setFill(Color.WHITE);
        this.getChildren().add(line);
        Button deleteReview = new Button();
        deleteReview.setText("Delete Review");
        deleteReview.setCursor(Cursor.HAND);
        deleteReview.setOnMouseClicked(this::deleteReview);
        Text username = new Text(user);
        username.setFill(Color.WHITE);
        username.setFont(Font.font("System", FontWeight.BOLD, 12));
        Text comment = new Text(text);
        comment.setFill(Color.WHITE);
        this.getChildren().addAll(username, comment, deleteReview);
    }

    private void deleteReview(MouseEvent mouseEvent) {
        Persistence dbManager = LayoutManagerFactory.getManager().dbManager;
        if(dbManager != null){
            if(dbManager.deleteReview(reviewID, song)){
                this.setVisible(false);
                this.setManaged(false);
            }
        }
    }
}
