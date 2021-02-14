package it.unipi.iit.inginf.lsmdb.communitunes.frontend.components;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.User;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.ArtistPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.UserPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.LayoutManager;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.LayoutManagerFactory;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.events.ArtistPreviewClickedEvent;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.events.UserPreviewClickedEvent;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.Persistence;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.PersistenceFactory;
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
        else{
            this.addEventHandler(USER_PREVIEW_CLICKED, this::defaultAction);
        }
    }

    public void defaultAction(UserPreviewClickedEvent event) {
        LayoutManager manager = LayoutManagerFactory.getManager();
        manager.goToUserPage(preview.username);
    }

    static final EventType<UserPreviewClickedEvent> USER_PREVIEW_CLICKED = new EventType<>(PREVIEW_CLICKED, "USER_PREVIEW_CLICKED");

    @Override
    protected void delete(MouseEvent mouseEvent) {
        Persistence dbManager = PersistenceFactory.CreatePersistence();
        if(dbManager.deleteUser(preview.username)){
            this.setVisible(false);
            this.setManaged(false);
        }
    }

    @Override
    protected void onMouseClicked(MouseEvent mouseEvent){
        UserPreviewClickedEvent userPreviewClickedEvent = new UserPreviewClickedEvent(USER_PREVIEW_CLICKED, this.preview);
        this.fireEvent(userPreviewClickedEvent);
    }
}
