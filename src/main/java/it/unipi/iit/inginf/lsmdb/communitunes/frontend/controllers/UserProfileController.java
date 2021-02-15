package it.unipi.iit.inginf.lsmdb.communitunes.frontend.controllers;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.User;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.ArtistPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.SongPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.UserPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.components.ArtistPreviewVBox;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.components.SongPreviewVBox;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.components.UserPreviewVBox;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.LayoutManager;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.LayoutManagerFactory;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.Path;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.Persistence;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.PersistenceFactory;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
    public Button userAnalytics;
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
    public HBox followerArtistsBox;
    public Button reportBtn;
    public Text reportText;

    private User user;
    private LayoutManager manager;
    private Persistence dbManager;

    private boolean followingFocusedUser;

    @Override
    public void init() {
        this.manager = LayoutManagerFactory.getManager();
        user = manager.context.getFocusedUser();
        dbManager = PersistenceFactory.CreatePersistence();
        username.setText(user.username);
        Image avatar;
        if(user.image != null){
            try{
                avatar = new Image(user.image);
                avatar.errorProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                        if(!aBoolean && t1){
                            Image def = new Image(this.getClass().getResourceAsStream("/ui/img/user_default.png"));
                            avatarCircle.setFill(new ImagePattern(def));
                        }
                    }
                });
                avatarCircle.setFill(new ImagePattern(avatar));
            }
            catch(Exception exc) {
                avatar = new Image(this.getClass().getResourceAsStream("/ui/img/user_default.png"));
                avatarCircle.setFill(new ImagePattern(avatar));
            }
        }
        else {
            Image def = new Image(this.getClass().getResourceAsStream("/ui/img/user_default.png"));
            avatarCircle.setFill(new ImagePattern(def));
        }

        // check if the profile belongs to the user that is currently logged in
        if(manager.context.getAuthenticatedUser() != null &&
                manager.context.getAuthenticatedUser().username != null &&
                manager.context.getAuthenticatedUser().username.equals(manager.context.getFocusedUser().username)){
            editProfile.setVisible(true);
            editProfile.setManaged(true);
            userAnalytics.setVisible(true);
            userAnalytics.setManaged(true);
            followUnfollow.setVisible(false);
            followUnfollow.setManaged(false);
            userAnalytics.setManaged(true);
            userAnalytics.setVisible(true);
            reportBtn.setManaged(false);
            reportBtn.setVisible(false);
        }
        else{
            editProfile.setVisible(false);
            editProfile.setManaged(false);
            userAnalytics.setVisible(false);
            userAnalytics.setManaged(false);
            followUnfollow.setVisible(true);
            followUnfollow.setManaged(true);
            userAnalytics.setManaged(false);
            userAnalytics.setVisible(false);
            if(!manager.context.inAdminPanel){
                if(dbManager.checkFollow(user, manager.context.getAuthenticatedUser())){
                    followUnfollow.setText("Unfollow");
                    followingFocusedUser = true;
                }
                else{
                    followUnfollow.setText("Follow");
                    followingFocusedUser = false;
                }
            }
            else{
                followUnfollow.setVisible(false);
                editProfile.setVisible(false);
                reportBtn.setVisible(false);
            }
        }

        reportText.setVisible(false);

        // write info in the infoContainer
        if(user.firstName != null){
            firstName.setText(user.firstName);
            firstNameBox.setVisible(true);
            firstNameBox.setManaged(true);
        }
        else {
            firstNameBox.setVisible(false);
            firstNameBox.setManaged(false);
        }
        if(user.lastName != null){
            lastName.setText(user.lastName);
            lastNameBox.setVisible(true);
            lastNameBox.setManaged(true);
        }
        else{
            lastNameBox.setVisible(false);
            lastNameBox.setManaged(false);
        }
        if(user.country != null){
            country.setText(user.country);
            countryBox.setVisible(true);
            countryBox.setManaged(true);
        }
        else{
            countryBox.setVisible(false);
            countryBox.setManaged(false);
        }
        if(user.birthday != null){
            birthday.setText(user.birthday.toString());
            birthdayBox.setVisible(true);
            birthdayBox.setManaged(true);
        }
        else{
            birthdayBox.setVisible(false);
            birthdayBox.setManaged(false);
        }

        for(SongPreview preview : user.likes){
            likesHBox.getChildren().add(new SongPreviewVBox(preview, null));
        }

        if(likesHBox.getChildren().isEmpty()){
            likesHBox.getParent().setVisible(false);
            likesHBox.getParent().setManaged(false);
        }

        for(UserPreview preview : user.followed){
            followedBox.getChildren().add(new UserPreviewVBox(preview, null));
        }

        if(followedBox.getChildren().isEmpty()){
            followedBox.getParent().setVisible(false);
            followedBox.getParent().setManaged(false);
        }

        for(UserPreview preview : user.followers){
            followersBox.getChildren().add(new UserPreviewVBox(preview, null));
        }

        if(followersBox.getChildren().isEmpty()){
            followersBox.getParent().setVisible(false);
            followersBox.getParent().setManaged(false);
        }

        for(ArtistPreview preview : user.followedArtists){
            followedArtistsBox.getChildren().add(new ArtistPreviewVBox(preview, null));
        }

        if(followedArtistsBox.getChildren().isEmpty()){
            followedArtistsBox.getParent().setVisible(false);
            followedArtistsBox.getParent().setManaged(false);
        }

        for(ArtistPreview preview : user.followerArtists){
            followerArtistsBox.getChildren().add(new ArtistPreviewVBox(preview, null));
        }

        if(followerArtistsBox.getChildren().isEmpty()){
            followerArtistsBox.getParent().setVisible(false);
            followerArtistsBox.getParent().setManaged(false);
        }

    }

    public void goToAnalytics(MouseEvent mouseEvent) throws IOException {
        manager.setContent(Path.USER_ANALYTICS);
    }

    public void goToEdit(MouseEvent mouseEvent) throws IOException {
        manager.setContent(Path.USER_EDIT);
    }

    public void showAllFollowedArtists(MouseEvent mouseEvent) throws IOException {
        manager.setContent(Path.FOLLOWED_ARTISTS);
    }

    public void showAllFollowers(MouseEvent mouseEvent) throws IOException {
        manager.setContent(Path.FOLLOWERS_PAGE);
    }

    public void showAllFollowed(MouseEvent mouseEvent) throws IOException{
        manager.setContent(Path.FOLLOWED_PAGE);
    }

    public void showAllArtistFollowers(MouseEvent mouseEvent) throws IOException {
        manager.setContent(Path.FOLLOWER_ARTISTS);
    }

    public void showAllLikes(MouseEvent mouseEvent) throws IOException {
        manager.setContent(Path.LIKED_SONGS);
    }

    public void followUser(MouseEvent mouseEvent) {
        User current = manager.context.getAuthenticatedUser();
        User toFollow = manager.context.getFocusedUser();
        if(!followingFocusedUser){
            if(dbManager.addFollow(toFollow, current)){
                followUnfollow.setText("Unfollow");
                followingFocusedUser = true;
            }
        }
        else{
            if(dbManager.deleteFollow(toFollow, current)){
                followUnfollow.setText("Follow");
                followingFocusedUser = false;
            }
        }
    }

    public void reportUser(MouseEvent mouseEvent) {
        if(dbManager.reportUser(user)){
            reportText.setVisible(true);
            reportBtn.setDisable(true);
        }
    }
}
