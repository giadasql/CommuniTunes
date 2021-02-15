package it.unipi.iit.inginf.lsmdb.communitunes.frontend.components.previews;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.SongPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.LayoutManager;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.LayoutManagerFactory;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.events.SongPreviewClickedEvent;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.Persistence;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.PersistenceFactory;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;

public class SongPreviewVBox extends PreviewVBox {
    public SongPreview preview;

    public SongPreviewVBox(SongPreview preview, EventHandler<SongPreviewClickedEvent> onMouseClickedEventHandler) {
        super(preview.Image, preview.Title);
        this.preview = preview;
        if(onMouseClickedEventHandler != null){
            this.addEventHandler(SONG_PREVIEW_CLICKED, onMouseClickedEventHandler);
        }
        else{
            this.addEventHandler(SONG_PREVIEW_CLICKED, this::defaultAction);
        }
    }

    static final EventType<SongPreviewClickedEvent> SONG_PREVIEW_CLICKED = new EventType<>(PREVIEW_CLICKED, "SONG_PREVIEW_CLICKED");

    protected void defaultAction(SongPreviewClickedEvent event) {
        LayoutManager manager = LayoutManagerFactory.getManager();
        manager.goToSongPage(preview.ID);
    }

    @Override
    protected void delete(MouseEvent mouseEvent) {
        Persistence dbManager = PersistenceFactory.CreatePersistence();
        if(dbManager.deleteSong(preview.ID)){
            this.setVisible(false);
            this.setManaged(false);
        }
    }

    @Override
    protected void onMouseClicked(MouseEvent mouseEvent){
        SongPreviewClickedEvent songPreviewClickedEvent = new SongPreviewClickedEvent(SONG_PREVIEW_CLICKED, this.preview);
        this.fireEvent(songPreviewClickedEvent);
    }

    @Override
    public void setDefaultImage(){
        Image image = new Image(this.getClass().getResourceAsStream("/ui/img/song_default.png"));
        imageView.setImage(image);
    }

}
