package it.unipi.iit.inginf.lsmdb.communitunes.frontend.controllers.general;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.ArtistPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.SongPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.components.HomepageAnchorPane;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.LayoutManager;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.LayoutManagerFactory;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.controllers.UIController;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.Persistence;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.PersistenceFactory;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

import java.util.*;

public class HomePageController implements UIController {

    @FXML
    private VBox mainVbox;

    private LayoutManager manager;
    private Persistence dbManager;

    @Override
    public void init() {
        this.manager = LayoutManagerFactory.getManager();
        dbManager = PersistenceFactory.CreatePersistence();
        Map<String, List<SongPreview>> songsList = dbManager.getBestSongsForEachGenre();
        Map<String, ArtistPreview> artistsList = dbManager.getRepresentativeArtist();
        System.out.println("ok");
        for (String genre:
             artistsList.keySet()) {
            ArtistPreview representativeArtist = artistsList.getOrDefault(genre, null);
            List<SongPreview> bestSongs = songsList.getOrDefault(genre, new ArrayList<>());
            mainVbox.getChildren().add(new HomepageAnchorPane(genre, bestSongs, representativeArtist));
        }
    }
}
