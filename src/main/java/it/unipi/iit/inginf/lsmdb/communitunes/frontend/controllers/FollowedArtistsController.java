package it.unipi.iit.inginf.lsmdb.communitunes.frontend.controllers;

import it.unipi.iit.inginf.lsmdb.communitunes.authentication.AuthenticationFactory;
import it.unipi.iit.inginf.lsmdb.communitunes.authentication.AuthenticationManager;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.User;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.ArtistPreview;
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

public class FollowedArtistsController implements UIController {

    public VBox mainVbox;

    private User user;
    private Persistence dbManager;
    private AuthenticationManager authManager;
    private LayoutManager manager;

    @Override
    public void init(){
        manager = LayoutManagerFactory.getManager();
        user = manager.context.getFocusedUser();
        dbManager = PersistenceFactory.CreatePersistence();
        authManager = AuthenticationFactory.CreateAuthenticationManager();

        user.LoadedArtistFollowed = dbManager.getFollowedArtists(user.Username);
        for(Iterator<ArtistPreview> iter = user.LoadedArtistFollowed.iterator(); iter.hasNext(); ){
            List<ArtistPreview> list = new ArrayList<>();
            for(int i = 0; i < 6; i++){
                ArtistPreview artist;
                if(!iter.hasNext())
                    break;
                artist = iter.next();
                list.add(artist);
            }


            mainVbox.getChildren().add(new ListHbox().buildArtistList(list));
        }
    }

    public void closeWindow(MouseEvent mouseEvent) throws IOException {
        manager.setContent(Path.USER_PROFILE);
    }
}
