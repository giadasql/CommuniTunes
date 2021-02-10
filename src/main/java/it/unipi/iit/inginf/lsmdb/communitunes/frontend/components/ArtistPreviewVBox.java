package it.unipi.iit.inginf.lsmdb.communitunes.frontend.components;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.ArtistPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.events.ArtistPreviewClickedEvent;
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

public class ArtistPreviewVBox extends PreviewVBox {
    public ArtistPreview preview;

    public ArtistPreviewVBox(ArtistPreview preview, EventHandler<ArtistPreviewClickedEvent> onMouseClickedEventHandler) {
        super(preview.image, preview.username);
        this.preview = preview;
        if(onMouseClickedEventHandler != null){
            this.addEventHandler(ARTIST_PREVIEW_CLICKED, onMouseClickedEventHandler);
        }
    }

    static final EventType<ArtistPreviewClickedEvent> ARTIST_PREVIEW_CLICKED = new EventType<>(PREVIEW_CLICKED, "PREVIEW_CLICKED");

    @Override
    protected void onMouseClicked(MouseEvent mouseEvent){
        ArtistPreviewClickedEvent artistClickedEvent = new ArtistPreviewClickedEvent(ARTIST_PREVIEW_CLICKED, this.preview);
        this.fireEvent(artistClickedEvent);
    }
}
