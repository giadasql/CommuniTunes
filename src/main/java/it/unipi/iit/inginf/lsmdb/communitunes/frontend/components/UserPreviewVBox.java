package it.unipi.iit.inginf.lsmdb.communitunes.frontend.components;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.User;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.ArtistPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.UserPreview;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class UserPreviewVBox extends VBox {
    public UserPreview preview;

    public UserPreviewVBox(UserPreview preview) {
        this.preview = preview;
        super.setWidth(130);
        super.setHeight(150);
        super.setAlignment(Pos.CENTER);
        ImageView imageView = new ImageView();
        imageView.setFitWidth(this.getWidth());
        imageView.setFitHeight(this.getHeight() - 30);
        if(preview.image != null){
            try{
                Image image = new Image(preview.image, true);
                if (image.isError()) {
                    image = new Image(this.getClass().getResourceAsStream("/ui/img/profile-user.png"));
                    imageView.setImage(image);
                }
            }
            catch(Exception exc) {
                Image image = new Image(this.getClass().getResourceAsStream("/ui/img/profile-user.png"));
                imageView.setImage(image);
            }
        }
        super.getChildren().add(imageView);
        Text text = new Text(preview.username);
        text.setFill(Color.WHITE);
        text.setFont(new Font("Book Antiqua", 15));
        text.setTextAlignment(TextAlignment.CENTER);
        super.getChildren().add(text);
    }
}
