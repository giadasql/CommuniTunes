package it.unipi.iit.inginf.lsmdb.communitunes.frontend.controllers;

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
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.Persistence;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.PersistenceFactory;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.Map;
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
    public Button addSongBtn;
    public Button reportBtn;
    public Text reportText;
    public Text bestAlbum;
    public Text worstAlbum;

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
        username.setText(artist.username);
        Image avatar;
        if(artist.image != null){
            try{
                avatar = new Image(artist.image);
                if (avatar.isError()) {
                    avatar = new Image(this.getClass().getResourceAsStream("/ui/img/user_default.png"));
                }
                avatarCircle.setFill(new ImagePattern(avatar));
            }
            catch(Exception exc) {
                avatar = new Image(this.getClass().getResourceAsStream("/ui/img/user_default.png"));
                avatarCircle.setFill(new ImagePattern(avatar));
            }
        }


        // check if the profile belongs to the user that is currently logged in
        if(manager.context.getAuthenticatedUser() != null &&
                manager.context.getAuthenticatedUser().username != null &&
                manager.context.getAuthenticatedUser().username.equals(artist.username)){
            editProfile.setVisible(true);
            editProfile.setManaged(true);
            followUnfollow.setVisible(false);
            followUnfollow.setManaged(false);
            analyticsArtist.setVisible(true);
            analyticsArtist.setManaged(true);
            addSongBtn.setVisible(true);
            addSongBtn.setManaged(true);
            reportBtn.setManaged(false);
            reportBtn.setVisible(false);
        }
        else {
            editProfile.setVisible(false);
            editProfile.setManaged(false);
            followUnfollow.setVisible(true);
            followUnfollow.setManaged(true);
            analyticsArtist.setVisible(false);
            analyticsArtist.setManaged(false);
            addSongBtn.setVisible(false);
            addSongBtn.setManaged(false);
            if(!manager.context.inAdminPanel){
                if(dbManager.checkFollow(artist, manager.context.getAuthenticatedUser())){
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

        Map<String, String> bestAndWorst = dbManager.getBestAndWorstAlbum(artist);
        if(bestAndWorst != null){
            String best = bestAndWorst.get("best");
            if(best != null){
                bestAlbum.setText(best);
            }
            else{
                bestAlbum.setVisible(false);
                bestAlbum.setManaged(false);
            }
            String worst = bestAndWorst.get("worst");
            if(worst != null){
                worstAlbum.setText(worst);
            }
            else{
                worstAlbum.setVisible(false);
                worstAlbum.setManaged(false);
            }
        }

        // write info in the infoContainer
        if(artist.firstName != null){
            firstName.setText(artist.firstName);
            firstNameBox.setVisible(true);
            firstNameBox.setManaged(true);
        }
        else {
            firstNameBox.setVisible(false);
            firstNameBox.setManaged(false);
        }

        if(artist.lastName != null){
            lastName.setText(artist.lastName);
            lastNameBox.setVisible(true);
            lastNameBox.setManaged(true);
        }
        else{
            lastNameBox.setVisible(false);
            lastNameBox.setManaged(false);
        }

        if(artist.stageName != null){
            stageName.setText(artist.stageName);
            stageNameBox.setVisible(true);
            stageNameBox.setManaged(true);
        }
        else{
            stageNameBox.setVisible(false);
            stageNameBox.setManaged(false);
        }

        if(!artist.links.isEmpty()){
            for (Link link : artist.links.stream().limit(4).collect(Collectors.toList())){
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

        if(artist.country != null){
            country.setText(artist.country);
            countryBox.setVisible(true);
            countryBox.setManaged(true);
        }
        else{
            countryBox.setVisible(false);
            countryBox.setManaged(false);
        }
        if(artist.birthday != null){
            birthday.setText(artist.birthday.toString());
            birthdayBox.setVisible(true);
            birthdayBox.setManaged(true);
        }
        else{
            birthdayBox.setVisible(false);
            birthdayBox.setManaged(false);
        }
        
        if(artist.activeYears != null){
            activity.setText(artist.activeYears);
            activityBox.setVisible(true);
            activityBox.setManaged(true);
        }
        else{
            activityBox.setVisible(false);
            activityBox.setManaged(false);
        }

        if(artist.biography != null){
            if(artist.biography.length() > 150){
                biography.setText(artist.biography.substring(0, 150) + "... ");
                readMoreOrLess.setText("Read more");
                readMoreOrLess.setOnAction(this::toggleBiography);
            }
            else{
                biography.setText(artist.biography);
            }
        }

        if(artist.songPreviews != null){
            for(SongPreview songPreview : artist.songPreviews){
                    songsHbox.getChildren().add(new SongPreviewVBox(songPreview, null));
            }
        }

        for(UserPreview preview : artist.followed){
            followedBox.getChildren().add(new UserPreviewVBox(preview, null));
        }

        if(followedBox.getChildren().isEmpty()){
            followedBox.getParent().setVisible(false);
            followedBox.getParent().setManaged(false);
        }

        for(UserPreview preview : artist.followers){
            followersBox.getChildren().add(new UserPreviewVBox(preview, null));
        }

        if(followersBox.getChildren().isEmpty()){
            followersBox.getParent().setVisible(false);
            followersBox.getParent().setManaged(false);
        }

        for(ArtistPreview preview : artist.followedArtists){
            followedArtistsBox.getChildren().add(new ArtistPreviewVBox(preview, null));
        }

        if(followedArtistsBox.getChildren().isEmpty()){
            followedArtistsBox.getParent().setVisible(false);
            followedArtistsBox.getParent().setManaged(false);
        }

        for(ArtistPreview preview : artist.followerArtists){
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
            biography.setText(artist.biography.substring(0, 150) + "... ");
            readMoreOrLess.setText("Read more");
        }
        else{
            biographyExpanded = true;
            biography.setText(artist.biography);
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

    public void reportUser(MouseEvent mouseEvent) {
        if(dbManager.reportUser(artist)){
            reportText.setVisible(true);
            reportBtn.setDisable(true);
        }
    }
}
