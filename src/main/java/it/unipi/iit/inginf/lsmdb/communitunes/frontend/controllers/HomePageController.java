package it.unipi.iit.inginf.lsmdb.communitunes.frontend.controllers;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.User;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.LayoutManager;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.LayoutManagerFactory;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.Persistence;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.PersistenceFactory;

public class HomePageController implements UIController {

    private User user;
    private LayoutManager manager;
    private Persistence dbManager;

    @Override
    public void init() {
        this.manager = LayoutManagerFactory.getManager();
        user = manager.context.getFocusedUser();
        dbManager = PersistenceFactory.CreatePersistence();
    }
}
