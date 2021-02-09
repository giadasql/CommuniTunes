package it.unipi.iit.inginf.lsmdb.communitunes.frontend.controllers;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.Artist;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.User;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.ArtistPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.SongPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.UserPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.components.ArtistPreviewVBox;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.components.SongPreviewVBox;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.components.UserPreviewVBox;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.LayoutManager;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.Path;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.io.IOException;

public class UserProfileController implements UIController {
    @FXML
    public Text username;
    @FXML
    public Pane userInfo;
    public Button followUnfollow;
    public Circle avatarCircle;
    public Button editProfile;
    public VBox infoVBox;
    public Text country;
    public Text birthday;
    public Text lastName;
    public Text firstName;
    public HBox lastNameBox;
    public HBox firstNameBox;
    public HBox countryBox;
    public HBox birthdayBox;
    public HBox likesHBox;
    public HBox followedArtistsBox;
    public HBox followersBox;
    public HBox followedBox;
    public HBox artistsFollowerBox;

    private User user;
    private LayoutManager manager;

    @Override
    public void init(LayoutManager manager) {
        this.manager = manager;
        user = manager.context.getFocusedUser();
        username.setText(user.Username);
        Image avatar;
        if(user.Image != null){
            try{
                avatar = new Image(user.Image);
                if (avatar.isError()) {
                    avatar = new Image(this.getClass().getResourceAsStream("/ui/img/profile-user.png"));
                }
                avatarCircle.setFill(new ImagePattern(avatar));
            }
            catch(Exception exc) {
                avatar = new Image(this.getClass().getResourceAsStream("/ui/img/profile-user.png"));
                avatarCircle.setFill(new ImagePattern(avatar));
            }
        }


        // check if the profile belongs to the user that is currently logged in
        if(manager.context.getAuthenticatedUser().Username.equals(user.Username)){
            editProfile.setVisible(true);
            editProfile.setManaged(true);
            followUnfollow.setVisible(false);
            followUnfollow.setManaged(false);
        }
        else{
            editProfile.setVisible(false);
            editProfile.setManaged(false);
            followUnfollow.setVisible(true);
            followUnfollow.setManaged(true);
        }
        // write info in the infoContainer
        if(user.FirstName != null){
            firstName.setText(user.FirstName);
            firstNameBox.setVisible(true);
            firstNameBox.setManaged(true);
        }
        else {
            firstNameBox.setVisible(false);
            firstNameBox.setManaged(false);
        }
        if(user.LastName != null){
            lastName.setText(user.LastName);
            lastNameBox.setVisible(true);
            lastNameBox.setManaged(true);
        }
        else{
            lastNameBox.setVisible(false);
            lastNameBox.setManaged(false);
        }
        if(user.Country != null){
            country.setText(user.Country);
            countryBox.setVisible(true);
            countryBox.setManaged(true);
        }
        else{
            countryBox.setVisible(false);
            countryBox.setManaged(false);
        }
        if(user.Birthday != null){
            birthday.setText(user.Birthday.toString());
            birthdayBox.setVisible(true);
            birthdayBox.setManaged(true);
        }
        else{
            birthdayBox.setVisible(false);
            birthdayBox.setManaged(false);
        }

        for(SongPreview preview : user.LoadedLikes){
            likesHBox.getChildren().add(new SongPreviewVBox(preview));
        }

        if(likesHBox.getChildren().isEmpty()){
            likesHBox.getParent().setVisible(false);
            likesHBox.getParent().setManaged(false);
        }

        for(UserPreview preview : user.LoadedFollowed){
            followedBox.getChildren().add(new UserPreviewVBox(preview));
        }

        for(UserPreview preview : user.LoadedFollowers){
            followersBox.getChildren().add(new UserPreviewVBox(preview));
        }

        for(ArtistPreview preview : user.LoadedArtistFollowed){
            followedArtistsBox.getChildren().add(new ArtistPreviewVBox(preview));
        }

        for(ArtistPreview preview : user.LoadedArtistFollowers){
            artistsFollowerBox.getChildren().add(new ArtistPreviewVBox(preview));
        }
    }

    public void goToEdit(MouseEvent mouseEvent) throws IOException {
        manager.setContent(Path.USER_EDIT);
    }
}
