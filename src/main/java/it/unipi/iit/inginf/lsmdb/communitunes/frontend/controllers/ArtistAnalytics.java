package it.unipi.iit.inginf.lsmdb.communitunes.frontend.controllers;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.Artist;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.ArtistPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.SongPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.UserPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.components.ArtistPreviewVBox;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.components.SongPreviewVBox;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.components.UserPreviewVBox;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.LayoutManager;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.LayoutManagerFactory;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.Path;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.Persistence;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.PersistenceFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.List;

public class ArtistAnalytics implements UIController {

    public HBox artistColleague;
    public HBox similarArtists;
    public HBox topFans;
    public HBox popularSongs;

    private Artist artist;
    private LayoutManager manager;
    private Persistence dbManager;

    public void init() {
        this.manager = LayoutManagerFactory.getManager();
        dbManager = PersistenceFactory.CreatePersistence();
        artist = manager.context.getAuthenticatedArtist();
        List<ArtistPreview> similarArtistsList = dbManager.getSimilarArtists(artist);
        List<UserPreview> topFansList = dbManager.getTopFans(artist);
        List<SongPreview> popularSongsList = dbManager.getPopularSongs(artist);

        for(ArtistPreview artist : similarArtistsList){
            similarArtists.getChildren().add(new ArtistPreviewVBox(artist, null));
        }
        if(similarArtists.getChildren().isEmpty()){
            similarArtists.getParent().setVisible(false);
            similarArtists.getParent().setManaged(false);
        }

        for(UserPreview preview : topFansList){
            topFans.getChildren().add(new UserPreviewVBox(preview, null));
        }
        if(topFans.getChildren().isEmpty()){
            topFans.getParent().setVisible(false);
            topFans.getParent().setManaged(false);
        }

        for(SongPreview song : popularSongsList){
            popularSongs.getChildren().add(new SongPreviewVBox(song, null));
        }
        if(popularSongs.getChildren().isEmpty()){
            popularSongs.getParent().setVisible(false);
            popularSongs.getParent().setManaged(false);
        }
    }

    public void closeWindow(MouseEvent mouseEvent) throws IOException {
        manager.setContent(Path.USER_PROFILE);
    }
}
