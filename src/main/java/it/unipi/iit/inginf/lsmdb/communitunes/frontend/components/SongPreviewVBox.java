package it.unipi.iit.inginf.lsmdb.communitunes.frontend.components;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.SongPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.events.ArtistPreviewClickedEvent;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.events.SongPreviewClickedEvent;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class SongPreviewVBox extends PreviewVBox {
    public SongPreview preview;

    public SongPreviewVBox(SongPreview preview, EventHandler<SongPreviewClickedEvent> onMouseClickedEventHandler) {
        super(preview.Image, preview.Title);
        this.preview = preview;
        if(onMouseClickedEventHandler != null){
            this.addEventHandler(SONG_PREVIEW_CLICKED, onMouseClickedEventHandler);
        }
    }

    static final EventType<SongPreviewClickedEvent> SONG_PREVIEW_CLICKED = new EventType<>(PREVIEW_CLICKED, "SONG_PREVIEW_CLICKED");

    @Override
    protected void onMouseClicked(MouseEvent mouseEvent){
        SongPreviewClickedEvent songPreviewClickedEvent = new SongPreviewClickedEvent(SONG_PREVIEW_CLICKED, this.preview);
        this.fireEvent(songPreviewClickedEvent);
    }

}
