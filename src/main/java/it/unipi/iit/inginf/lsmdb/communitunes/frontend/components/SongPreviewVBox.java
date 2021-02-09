package it.unipi.iit.inginf.lsmdb.communitunes.frontend.components;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.SongPreview;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class SongPreviewVBox extends VBox {
    public SongPreview preview;

    public SongPreviewVBox(SongPreview preview) {
        this.preview = preview;
        super.setWidth(130);
        super.setHeight(150);
        super.setAlignment(Pos.CENTER);
        ImageView imageView = new ImageView();
        imageView.setFitWidth(this.getWidth());
        imageView.setFitHeight(this.getHeight() - 30);
        if (preview.Image != null) {
            Image image = new Image(preview.Image, true);
            imageView.setImage(image);
        }
        super.getChildren().add(imageView);
        Text text = new Text(preview.Title);
        text.setFill(Color.WHITE);
        text.setFont(new Font("Book Antiqua", 15));
        text.setTextAlignment(TextAlignment.CENTER);
        super.getChildren().add(text);
    }

}
