package it.unipi.iit.inginf.lsmdb.communitunes.frontend.controllers;

import it.unipi.iit.inginf.lsmdb.communitunes.authentication.AuthenticationFactory;
import it.unipi.iit.inginf.lsmdb.communitunes.authentication.AuthenticationManager;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.User;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.UserPreview;
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

public class FollowersController implements UIController {

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

        user.LoadedFollowers = dbManager.getFollowers(user.Username);
        for(Iterator<UserPreview> iter = user.LoadedFollowers.iterator(); iter.hasNext(); ){
            List<UserPreview> list = new ArrayList<>();
            for(int i = 0; i < 6; i++){
                UserPreview user;
                if(!iter.hasNext())
                    break;
                user = iter.next();
                list.add(user);
            }

            mainVbox.getChildren().add(new ListHbox().buildUserList(list));
        }
    }

    public void closeWindow(MouseEvent mouseEvent) throws IOException {
        manager.setContent(Path.USER_PROFILE);
    }
}
