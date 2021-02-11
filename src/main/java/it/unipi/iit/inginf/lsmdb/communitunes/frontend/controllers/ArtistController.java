package it.unipi.iit.inginf.lsmdb.communitunes.frontend.controllers;

import com.sun.javafx.application.HostServicesDelegate;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.Artist;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.Link;
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
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.events.SongPreviewClickedEvent;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.Persistence;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.PersistenceFactory;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.stream.Collectors;

public class ArtistController implements UIController {

    public Text username;
    public Button editProfile;
    public Button analyticsArtist;
    public Button followUnfollow;
    public Text biography;
    public HBox lastNameBox;
    public Text lastName;
    public HBox sitesBox;
    public Text websites;
    public HBox activityBox;
    public Text activity;
    public HBox songsHbox;
    public Text stageName;
    public HBox stageNameBox;
    public VBox infoVBox;
    public Circle avatarCircle;
    public HBox firstNameBox;
    public Text firstName;
    public VBox links;
    public Hyperlink readMoreOrLess;
    public HBox countryBox;
    public Text country;
    public HBox birthdayBox;
    public Text birthday;
    public HBox followedArtistsBox;
    public HBox followersBox;
    public HBox followedBox;
    public HBox followerArtistsBox;

    private Artist artist;
    private LayoutManager manager;
    private Persistence dbManager;

    private boolean biographyExpanded = false;
    private boolean followingFocusedUser = false;

    @Override
    public void init() {
        this.manager = LayoutManagerFactory.getManager();
        artist = manager.context.getFocusedArtist();
        if(artist == null){
            return;
        }
        dbManager = PersistenceFactory.CreatePersistence();
        username.setText(artist.Username);
        Image avatar;
        if(artist.Image != null){
            try{
                avatar = new Image(artist.Image);
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
        if(manager.context.getAuthenticatedUser() != null &&
                manager.context.getAuthenticatedUser().Username != null &&
                manager.context.getAuthenticatedUser().Username.equals(artist.Username)){
            editProfile.setVisible(true);
            editProfile.setManaged(true);
            followUnfollow.setVisible(false);
            followUnfollow.setManaged(false);
        }
        else {
                editProfile.setVisible(false);
                editProfile.setManaged(false);
                followUnfollow.setVisible(true);
                followUnfollow.setManaged(true);
            if(dbManager.checkFollow(artist, manager.context.getAuthenticatedUser())){
                followUnfollow.setText("Unfollow");
                followingFocusedUser = true;
            }
            else{
                followUnfollow.setText("Follow");
                followingFocusedUser = false;
            }
        }

        // write info in the infoContainer
        if(artist.FirstName != null){
            firstName.setText(artist.FirstName);
            firstNameBox.setVisible(true);
            firstNameBox.setManaged(true);
        }
        else {
            firstNameBox.setVisible(false);
            firstNameBox.setManaged(false);
        }

        if(artist.LastName != null){
            lastName.setText(artist.LastName);
            lastNameBox.setVisible(true);
            lastNameBox.setManaged(true);
        }
        else{
            lastNameBox.setVisible(false);
            lastNameBox.setManaged(false);
        }

        if(artist.StageName != null){
            stageName.setText(artist.StageName);
            stageNameBox.setVisible(true);
            stageNameBox.setManaged(true);
        }
        else{
            stageNameBox.setVisible(false);
            stageNameBox.setManaged(false);
        }

        if(!artist.Links.isEmpty()){
            for (Link link : artist.Links.stream().limit(4).collect(Collectors.toList())){
                if(link != null && link.url != null){
                    Hyperlink hyperlink = new Hyperlink();
                    hyperlink.setText(link.name);
                    hyperlink.getStyleClass().add("artist-site");
                    hyperlink.setOnAction(e -> {
                        manager.context.hostServices.showDocument(link.url);
                    });
                    links.getChildren().add(hyperlink);
                }
            }
        }

        if(artist.Country != null){
            country.setText(artist.Country);
            countryBox.setVisible(true);
            countryBox.setManaged(true);
        }
        else{
            countryBox.setVisible(false);
            countryBox.setManaged(false);
        }
        if(artist.Birthday != null){
            birthday.setText(artist.Birthday.toString());
            birthdayBox.setVisible(true);
            birthdayBox.setManaged(true);
        }
        else{
            birthdayBox.setVisible(false);
            birthdayBox.setManaged(false);
        }
        
        if(artist.ActiveYears != null){
            activity.setText(artist.ActiveYears);
            activityBox.setVisible(true);
            activityBox.setManaged(true);
        }
        else{
            activityBox.setVisible(false);
            activityBox.setManaged(false);
        }

        if(artist.Biography != null){
            if(artist.Biography.length() > 150){
                biography.setText(artist.Biography.substring(0, 150) + "... ");
                readMoreOrLess.setText("Read more");
                readMoreOrLess.setOnAction(this::toggleBiography);
            }
            else{
                biography.setText(artist.Biography);
            }
        }

        if(artist.LoadedSongs != null){
            for(SongPreview songPreview : artist.LoadedSongs){
                    songsHbox.getChildren().add(new SongPreviewVBox(songPreview, null));
            }
        }

        for(UserPreview preview : artist.LoadedFollowed){
            followedBox.getChildren().add(new UserPreviewVBox(preview, null));
        }

        if(followedBox.getChildren().isEmpty()){
            followedBox.getParent().setVisible(false);
            followedBox.getParent().setManaged(false);
        }

        for(UserPreview preview : artist.LoadedFollowers){
            followersBox.getChildren().add(new UserPreviewVBox(preview, null));
        }

        if(followersBox.getChildren().isEmpty()){
            followersBox.getParent().setVisible(false);
            followersBox.getParent().setManaged(false);
        }

        for(ArtistPreview preview : artist.LoadedArtistFollowed){
            followedArtistsBox.getChildren().add(new ArtistPreviewVBox(preview, null));
        }

        if(followedArtistsBox.getChildren().isEmpty()){
            followedArtistsBox.getParent().setVisible(false);
            followedArtistsBox.getParent().setManaged(false);
        }

        for(ArtistPreview preview : artist.LoadedArtistFollowers){
            followerArtistsBox.getChildren().add(new ArtistPreviewVBox(preview, null));
        }

        if(followerArtistsBox.getChildren().isEmpty()){
            followerArtistsBox.getParent().setVisible(false);
            followerArtistsBox.getParent().setManaged(false);
        }
    }


    private void toggleBiography(ActionEvent actionEvent) {
        if(biographyExpanded){
            biographyExpanded = false;
            biography.setText(artist.Biography.substring(0, 150) + "... ");
            readMoreOrLess.setText("Read more");
        }
        else{
            biographyExpanded = true;
            biography.setText(artist.Biography);
            readMoreOrLess.setText("Read less");
        }
    }

    public void showAnalytics(MouseEvent mouseEvent) throws IOException {
        manager.setContent(Path.ARTIST_ANALYTICS);
    }

    public void showAllSongs(MouseEvent mouseEvent) throws IOException {
        manager.setContent(Path.ARTIST_SONGS);
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

    public void addSong(MouseEvent mouseEvent) throws IOException {
        manager.setContent(Path.ADD_SONG);
    }

    public void editProfile(MouseEvent mouseEvent) throws IOException {
        manager.setContent(Path.ARTIST_EDIT);
    }

    public void followArtist(MouseEvent mouseEvent) {
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
}
