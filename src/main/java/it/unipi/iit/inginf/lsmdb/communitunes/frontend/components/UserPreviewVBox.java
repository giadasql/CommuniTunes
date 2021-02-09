package it.unipi.iit.inginf.lsmdb.communitunes.frontend.components;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.User;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.ArtistPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.UserPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.events.ArtistPreviewClickedEvent;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.events.UserPreviewClickedEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class UserPreviewVBox extends PreviewVBox {
    public UserPreview preview;

    public UserPreviewVBox(UserPreview preview, EventHandler<UserPreviewClickedEvent> onMouseClickedEventHandler) {
        super(preview.image, preview.username);
        this.preview = preview;
        if(onMouseClickedEventHandler != null){
            this.addEventHandler(USER_PREVIEW_CLICKED, onMouseClickedEventHandler);
        }
    }

    static final EventType<UserPreviewClickedEvent> USER_PREVIEW_CLICKED = new EventType<>(PREVIEW_CLICKED, "USER_PREVIEW_CLICKED");

    @Override
    protected void onMouseClicked(MouseEvent mouseEvent){
        UserPreviewClickedEvent userPreviewClickedEvent = new UserPreviewClickedEvent(USER_PREVIEW_CLICKED, this.preview);
        this.fireEvent(userPreviewClickedEvent);
    }
}
