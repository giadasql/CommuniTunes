package it.unipi.iit.inginf.lsmdb.communitunes.frontend.components;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.Review;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.LayoutManagerFactory;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.Persistence;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.PersistenceFactory;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;


public class ReviewVBox extends VBox {
    private Review review;
    private Text reportedReview;
    private Button reportBtn;

    public ReviewVBox(Review review, boolean showDeleteButton, boolean showReportBtn) {
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
        userName.setText(review.user);
        userName.setOnMouseClicked(e -> {
            LayoutManagerFactory.getManager().goToUserOrArtistPage(review.user);
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
        rating.setText("Rating: " + review.rating + " /100");
        rating.setFont(Font.font("Book Antiqua", FontWeight.BOLD, 20));
        rating.setFill(Color.WHITE);
        this.getChildren().add(headerHBox);
        this.getChildren().add(rating);
        if(review.text != null){
            if(showReportBtn){
                reportedReview = new Text();
                reportedReview.setText("You reported this review.");
                reportedReview.setFill(Color.RED);
                reportedReview.setFont(Font.font("Book Antiqua", 20));
                reportBtn = new Button();
                reportBtn.setCursor(Cursor.HAND);
                reportBtn.setMaxHeight(25);
                reportBtn.setMaxWidth(25);
                reportBtn.setBackground(Background.EMPTY);
                ImageView imageView = new ImageView(new Image(this.getClass().getResourceAsStream("/ui/img/report.png")));
                reportBtn.setGraphic(imageView);
                reportBtn.setOnMouseClicked(this::reportReview);
                headerHBox.getChildren().addAll(reportBtn, reportedReview);
                reportedReview.setVisible(false);
            }
            Text commentLabel = new Text();
            Text comment = new Text();
            comment.setText(review.text);
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

    private void reportReview(MouseEvent mouseEvent) {
        if(LayoutManagerFactory.getManager().dbManager.reportReview(review)){
            reportedReview.setVisible(true);
            reportBtn.setDisable(true);
        }
    }

    private void deleteReview(MouseEvent mouseEvent) {
        Persistence dbManager = LayoutManagerFactory.getManager().dbManager;
        if(dbManager != null){
            dbManager.deleteReview(review);
            LayoutManagerFactory.getManager().goToSongPage(review.song);
        }
    }
}
