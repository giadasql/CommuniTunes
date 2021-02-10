package it.unipi.iit.inginf.lsmdb.communitunes.frontend.components;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.Review;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.UserPreview;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

public class ReviewHBox extends HBox {
    private Review review;

    public ReviewHBox(Review review) {
        this.review = review;
        Text userName = new Text();
        userName.setText("User: " + review.User);
        Text rating = new Text();
        rating.setText("Rating: " + review.Rating + "/100");
        this.getChildren().add(userName);
        this.getChildren().add(rating);
        if(review.Text != null){
            Text comment = new Text();
            comment.setText("Comment: " + review.Text);
            this.getChildren().add(comment);
        }
    }
}
