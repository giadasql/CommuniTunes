package it.unipi.iit.inginf.lsmdb.communitunes.frontend.components;

import it.unipi.iit.inginf.lsmdb.communitunes.frontend.events.UserPreviewClickedEvent;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public abstract class PreviewVBox extends VBox {
    protected ImageView imageView;
    protected Text text;
    protected Button deleteBtn;

    public PreviewVBox(String img, String name) {
        super.setWidth(125);
        super.setHeight(150);
        super.setAlignment(Pos.TOP_CENTER);
        imageView = new ImageView();
        imageView.setFitWidth(this.getWidth());
        imageView.setFitHeight(this.getHeight() - 30);
        if(img != null){
            try{
                Image image = new Image(img, true);
                image.errorProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                        if(!aBoolean && t1){
                            setDefaultImage();
                        }
                    }
                });
                if (image.isError()) {
                    setDefaultImage();
                }
                else {
                    imageView.setImage(image);
                }
            }
            catch(Exception exc) {
                setDefaultImage();
            }
        }
        else{
            setDefaultImage();
        }
        super.getChildren().add(imageView);
        Text text;
        if(name.length() >= 25){
            text = new Text(name.substring(0, 22) + "...");
        }
        else{
            text = new Text(name);
        }
        text.setFill(Color.WHITE);
        text.setWrappingWidth(115);

        text.setFont(new Font("Book Antiqua", 15));
        text.setTextAlignment(TextAlignment.CENTER);
        super.getChildren().add(text);
        imageView.setOnMouseClicked(this::onMouseClicked);
        text.setOnMouseClicked(this::onMouseClicked);
        imageView.setCursor(Cursor.HAND);
        text.setCursor(Cursor.HAND);
    }

    public PreviewVBox addDeleteBtn(){
        deleteBtn = new Button("Delete");
        deleteBtn.setOnMouseClicked(this::delete);
        this.getChildren().add(deleteBtn);
        return this;
    }

    protected abstract void delete(MouseEvent mouseEvent);

    protected abstract void onMouseClicked(MouseEvent mouseEvent);

    static final EventType<Event> PREVIEW_CLICKED = new EventType<>(Event.ANY, "PREVIEW_CLICKED");

    public void setDefaultImage(){
        Image image = new Image(this.getClass().getResourceAsStream("/ui/img/user_default.png"));
        imageView.setImage(image);
    }
}
