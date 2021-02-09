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
        if(user.Image != null && !user.Image.equals("")){
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

        SongPreview songp = new SongPreview("aaa", "artist", "asd", "A nice song", "https://www.officialcharts.com/media/648111/michael-jackson-rex.jpg?width=462.7906976744186&height=500");
        for(int i = 0; i < 6; i++){
            likesHBox.getChildren().add(new SongPreviewVBox(songp));
        }

        UserPreview userp = new UserPreview("this user", "https://images.vanityfair.it/wp-content/uploads/2020/09/28145100/donald-trump-portrait-850x1360.jpg");
        for(int i = 0; i < 6; i++){
            followedBox.getChildren().add(new UserPreviewVBox(userp));
        }

        for(int i = 0; i < 6; i++){
            followersBox.getChildren().add(new UserPreviewVBox(userp));
        }

        ArtistPreview artistp = new ArtistPreview("this artist", "Queen", "https://dvfnvgxhycwzf.cloudfront.net/media/SharedImage/imageFull/.f3noMS6U/SharedImage-53612.jpg?t=1a8ff15292f3d96da274");
        for(int i = 0; i < 6; i++){
            artistsFollowerBox.getChildren().add(new ArtistPreviewVBox(artistp));
            followedArtistsBox.getChildren().add(new ArtistPreviewVBox(artistp));
        }
    }

    public void goToEdit(MouseEvent mouseEvent) throws IOException {
        manager.setContent(Path.USER_EDIT);
    }
}
