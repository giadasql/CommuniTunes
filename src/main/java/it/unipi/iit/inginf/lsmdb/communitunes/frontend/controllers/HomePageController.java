package it.unipi.iit.inginf.lsmdb.communitunes.frontend.controllers;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.User;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.ArtistPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.SongPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.components.HomepageAnchorPane;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.LayoutManager;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.LayoutManagerFactory;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.Persistence;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.PersistenceFactory;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import org.javatuples.Pair;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class HomePageController implements UIController {

    @FXML
    private VBox mainVbox;

    private LayoutManager manager;
    private Persistence dbManager;

    @Override
    public void init() {
        this.manager = LayoutManagerFactory.getManager();
        dbManager = PersistenceFactory.CreatePersistence();
        Map<String, List<SongPreview>> songsList = dbManager.getSuggestedSongs();
        Map<String, ArtistPreview> artistsList = dbManager.getRepresentativeArtist();
        System.out.println("ok");
//        for(Pair<String, ArtistPreview> artist : artistsList ){
//            List<SongPreview> songs = songsList.get(artist.getValue0());
//            for(SongPreview song : songs){
//                System.out.println(song.Title);
//            }
//            mainVbox.getChildren().add(new HomepageAnchorPane(artist.getValue0(), songs, artist.getValue1()));
//        }
    }
}
