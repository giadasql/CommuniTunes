package it.unipi.iit.inginf.lsmdb.communitunes.frontend.controllers;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.User;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.LayoutManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

public class UserProfileController implements UIController {
    @FXML
    public Text username;
    @FXML
    public Pane userInfo;
    public Button followUnfollow;
    public Circle avatarCircle;
    public Button editProfile;

    private User user;

    @Override
    public void init(LayoutManager manager) {
        user = manager.context.focusedUser;
        if(manager.context.authenticatedUser.Username.equals(user.Username)){
            editProfile.setVisible(true);
            followUnfollow.setVisible(false);
        }
        else{
            editProfile.setVisible(false);
            followUnfollow.setVisible(true);
        }
    }
}
