package it.unipi.iit.inginf.lsmdb.communitunes.frontend.components;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.Review;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.UserPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.LayoutManager;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.LayoutManagerFactory;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.Persistence;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.PersistenceFactory;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;


public class ReviewVBox extends VBox {
    private Review review;

    public ReviewVBox(Review review, boolean showDeleteButton) {
        this.review = review;
        Line line = new Line();
        line.setStartX(0);
        line.setStartY(0);
        line.setEndX(150);
        line.setEndY(0);
        line.setStroke(Color.WHITE);
        this.getChildren().add(line);
        HBox headerHBox = new HBox();
        Text usernameLabel = new Text();
        usernameLabel.setText("Posted by ");
        usernameLabel.setFill(Color.WHITE);
        usernameLabel.setFont(new Font("Book Antiqua", 20));
        Text userName = new Text();
        userName.setFill(Color.WHITE);
        userName.setText(review.User);
        userName.setOnMouseClicked(e -> {
            LayoutManagerFactory.getManager().goToUserOrArtistPage(review.User);
        });
        userName.setFont(Font.font("Book Antiqua", FontWeight.BOLD, 20));
        userName.setCursor(Cursor.HAND);
        headerHBox.getChildren().add(usernameLabel);
        headerHBox.getChildren().add(userName);
        if(showDeleteButton){
            Button deleteBtn = new Button();
            deleteBtn.setCursor(Cursor.HAND);
            deleteBtn.setMaxHeight(25);
            deleteBtn.setMaxWidth(25);
            Pane fillPane = new Pane();
            headerHBox.getChildren().add(fillPane);
            HBox.setHgrow(fillPane, Priority.ALWAYS);
            deleteBtn.setBackground(Background.EMPTY);
            ImageView imageView = new ImageView(new Image(this.getClass().getResourceAsStream("/ui/img/trash-can.png")));
            deleteBtn.setGraphic(imageView);
            deleteBtn.setOnMouseClicked(this::deleteReview);
            headerHBox.getChildren().add(deleteBtn);
        }
        Text rating = new Text();
        rating.setText("Rating: " + review.Rating + "/100");
        rating.setFont(Font.font("Book Antiqua", FontWeight.BOLD, 20));
        rating.setFill(Color.WHITE);
        this.getChildren().add(headerHBox);
        this.getChildren().add(rating);
        if(review.Text != null){
            Text commentLabel = new Text();
            Text comment = new Text();
            comment.setText(review.Text);
            comment.setFill(Color.WHITE);
            comment.setFont(Font.font("Book Antiqua", 20));
            comment.setWrappingWidth(780);
            commentLabel.setFill(Color.WHITE);
            commentLabel.setText("Comment:");
            commentLabel.setFont(Font.font("Book Antiqua", FontWeight.BOLD, 20));
            this.getChildren().add(commentLabel);
            this.getChildren().add(comment);
        }
    }

    private void deleteReview(MouseEvent mouseEvent) {
        Persistence dbManager = PersistenceFactory.CreatePersistence();
        if(dbManager != null){
            dbManager.deleteReview(review);
            LayoutManagerFactory.getManager().goToSongPage(review.Song);
        }
    }
}
