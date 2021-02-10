package it.unipi.iit.inginf.lsmdb.communitunes.frontend.controllers;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.User;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.ArtistPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.SongPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.components.HomepageAnchorPane;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.LayoutManager;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.Persistence;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.PersistenceFactory;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import org.javatuples.Pair;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class HomePageController implements UIController {

    @FXML
    private VBox mainVbox;

    private LayoutManager manager;
    private Persistence dbManager;

    @Override
    public void init(LayoutManager manager) {
        this.manager = manager;
        dbManager = PersistenceFactory.CreatePersistence();
        HashMap<String, List<SongPreview>> songsList = dbManager.getSuggestedSongs();
        List<Pair<String, ArtistPreview>> artistsList = dbManager.getRepresentativeArtist();

        for(Iterator<Pair<String, ArtistPreview>> iter = artistsList.iterator(); iter.hasNext();){
            Pair<String,ArtistPreview> element = iter.next();
            List<SongPreview> songs = songsList.get(element.getValue0());
            HomepageAnchorPane nextPane = new HomepageAnchorPane(element.getValue0(), songs, element.getValue1());
            mainVbox.getChildren().add(nextPane);
        }
    }
}
