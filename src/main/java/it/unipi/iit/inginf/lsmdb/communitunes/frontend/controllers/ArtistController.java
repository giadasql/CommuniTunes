package it.unipi.iit.inginf.lsmdb.communitunes.frontend.controllers;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.Artist;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.User;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.LayoutManager;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.Persistence;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.PersistenceFactory;

public class ArtistController implements UIController {

    private Artist artist;
    private LayoutManager manager;
    private Persistence dbManager;

    @Override
    public void init(LayoutManager manager) {
        this.manager = manager;
        //artist = manager.context.focusedArtist;
        dbManager = PersistenceFactory.CreatePersistence();
    }
}
