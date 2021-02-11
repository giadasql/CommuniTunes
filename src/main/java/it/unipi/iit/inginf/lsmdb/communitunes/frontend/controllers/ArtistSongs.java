package it.unipi.iit.inginf.lsmdb.communitunes.frontend.controllers;

import it.unipi.iit.inginf.lsmdb.communitunes.authentication.AuthenticationFactory;
import it.unipi.iit.inginf.lsmdb.communitunes.authentication.AuthenticationManager;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.Artist;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.SongPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.components.ListHbox;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.LayoutManager;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.LayoutManagerFactory;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.Path;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.Persistence;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.PersistenceFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ArtistSongs implements UIController {

    public VBox mainVbox;

    private Artist artist;
    private Persistence dbManager;
    private AuthenticationManager authManager;
    private LayoutManager manager;

    @Override
    public void init(){
        manager = LayoutManagerFactory.getManager();
        artist = manager.context.getFocusedArtist();
        dbManager = PersistenceFactory.CreatePersistence();
        authManager = AuthenticationFactory.CreateAuthenticationManager();

        artist.LoadedSongs = dbManager.getArtistSongs(artist.Username);
        for(Iterator<SongPreview> iter = artist.LoadedSongs.iterator(); iter.hasNext(); ){
            List<SongPreview> list = new ArrayList<>();
            for(int i = 0; i < 6; i++){
                SongPreview song;
                if(!iter.hasNext())
                    break;
                song = iter.next();
                list.add(song);
            }

            mainVbox.getChildren().add(new ListHbox().buildSongList(list));
        }
    }

    public void closeWindow(MouseEvent mouseEvent) throws IOException {
        manager.setContent(Path.ARTIST_PROFILE);
    }
}
