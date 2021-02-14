package it.unipi.iit.inginf.lsmdb.communitunes.frontend.controllers;

import it.unipi.iit.inginf.lsmdb.communitunes.authentication.AuthenticationManager;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.*;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.ArtistPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.SongPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.UserPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.components.*;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.LayoutManager;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.LayoutManagerFactory;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.Path;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.events.*;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.Persistence;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.PersistenceFactory;
import it.unipi.iit.inginf.lsmdb.communitunes.utilities.exceptions.PersistenceInconsistencyException;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomePageAdminController implements UIController {

    public ChoiceBox<String> searchMenu;
    public TextField searchText;
    public VBox reportBox;
    public VBox requestBox;
    public VBox searchBox;
    public Text message;
    public AnchorPane reportPane;
    public ScrollPane requestPane;
    public Stage otherStage = new Stage();

    public String showingCommentsOf;
    public VBox searchVbox;
    public AnchorPane searchContainer;

    private Persistence dbManager;

    private static final Font FONT = new Font("Book Antiqua", 14.0);

    public void init(){
        dbManager = PersistenceFactory.CreatePersistence();

        List<Report> reportList = dbManager.getReports();
        List<Request> requestList = dbManager.getRequests();
        SearchBar bar = new SearchBar();
        this.searchContainer.getChildren().add(0, bar);
        bar.addEventHandler(SearchBar.FOUND_ARTISTS_EVENT, this::showArtistsPreviews);
        bar.addEventHandler(SearchBar.FOUND_SONGS_EVENT, this::showSongsPreviews);
        bar.addEventHandler(SearchBar.FOUND_USERS_EVENT, this::showUsersPreviews);
        bar.addEventHandler(SearchBar.FOUND_REVIEWS_EVENT, this::showReviews);
        for(Report report : reportList){
            reportBox.getChildren().add(new ReportView(report));
            reportBox.addEventHandler(ReportView.USER_DELETED_EVENT, this::userDeleted);
            reportBox.addEventHandler(ReportView.SHOW_USER_REVIEWS_EVENT, this::showComments);
            reportBox.addEventHandler(ReportView.REPORT_DELETED_EVENT, this::reportDeleted);
            reportBox.addEventHandler(ReportView.SEE_USER_PROFILE_CLICKED, this::seeUserProfile);
        }

        for(Request request : requestList){
            VBox req = new VBox();
            HBox buttonshbox = new HBox();
            Button upgradeButton = new Button("Upgrade");
            Button cancelButton = new Button("Cancel");
            buttonshbox.getChildren().addAll(upgradeButton, cancelButton);
            Text username = new Text("User: " + request.username);
            upgradeButton.getStyleClass().add("admin-btn");
            cancelButton.getStyleClass().add("admin-btn");
            Text stageName = new Text("Requested Stage Name: " + request.requestedStageName);
            upgradeButton.setOnMouseClicked((event)-> {
                if(addArtist(request.username, request.requestedStageName)) {
                    req.setVisible(false);
                    req.setManaged(false);
                }
            });
            cancelButton.setOnMouseClicked((event)->{
                if(deleteRequest(request.username)){
                    req.setVisible(false);
                    req.setManaged(false);
                }
            });

            req.setPrefHeight(95);
            req.setPrefWidth(401);
            username.setFont(FONT);
            upgradeButton.setPrefHeight(37);
            upgradeButton.setPrefWidth(96);
            cancelButton.setPrefHeight(37);
            cancelButton.setPrefWidth(96);

            req.getChildren().addAll(username, stageName, buttonshbox);
            requestBox.getChildren().add(req);
        }

        reportBox.setVisible(true);
        reportBox.setManaged(true);
        requestBox.setVisible(false);
        requestBox.setManaged(false);

    }

    private void seeUserProfile(SeeUserProfileClicked event) {
        String username = event.username;
        if(dbManager.userIsArtist(username)){
            Artist toFocus = dbManager.getArtist(username);
            LayoutManagerFactory.getManager().context.setFocusedArtist(toFocus);
            setContent(Path.ARTIST_PROFILE);
        }
        else{
            User toFocus = dbManager.getUser(username);
            LayoutManagerFactory.getManager().context.setFocusedUser(toFocus);
            setContent(Path.USER_PROFILE);
        }
    }

    public void setContent(String resource){
        FXMLLoader loader = getLoader(resource);
        try {
            Parent node = loader.load();
            UIController controller = loader.getController();
            controller.init();
            otherStage.setTitle("Explore");
            otherStage.setScene(new Scene(node, 900, 537));
            otherStage.show();
        } catch (IOException e) {
            // TODO: log
            e.printStackTrace();
        }
    }

    public void showArtistsPreviews(FoundArtistsEvent event){
        searchBox.getChildren().clear();
        for (ArtistPreview preview:
             event.foundArtists) {
            searchBox.getChildren().add(new ArtistPreviewVBox(preview, null).addDeleteBtn());
        }
    }

    public void showSongsPreviews(FoundSongsEvent event){
        searchBox.getChildren().clear();
        for (SongPreview preview:
                event.foundSongs) {
            searchBox.getChildren().add(new SongPreviewVBox(preview, null).addDeleteBtn());
        }
    }

    public void showUsersPreviews(FoundUsersEvent event){
        searchBox.getChildren().clear();
        for (UserPreview preview:
                event.foundUsers) {
            searchBox.getChildren().add(new UserPreviewVBox(preview, null).addDeleteBtn());
        }
    }

    private FXMLLoader getLoader(String resourcePath){
        return new FXMLLoader(
                getClass().getClassLoader().getResource(
                        resourcePath
                )
        );
    }

    private void reportDeleted(ReportDeletedEvent event) {
        reportBox.getChildren().remove(event.reportView);
    }

    private void userDeleted(UserDeletedEvent event) {
        reportBox.getChildren().remove(event.view);
        if(showingCommentsOf != null && showingCommentsOf.equals(event.username)){
            searchBox.getChildren().clear();
        }
        else if(event.username.equals(searchText.getText()) && searchMenu.getValue().toString().equals("User")){
            searchBox.getChildren().clear();
        }
    }

    public void showComments(ShowUserReviewsEvent event){
        searchBox.getChildren().clear();
        showingCommentsOf = event.report.reportedUser;
        List<Review> reviews = dbManager.getReviewsByUsername(showingCommentsOf);
        for(Review review : reviews){
            searchBox.getChildren().add(new AdminReviewView(event.report.reportedUser, review.ID, review.Text, review.Song));
        }
    }

    public void showReviews(FoundReviewsEvent event){
        searchBox.getChildren().clear();
        showingCommentsOf = event.user;
        List<Review> reviews = event.reviewList;
        for(Review review : reviews){
            searchBox.getChildren().add(new AdminReviewView(event.user, review.ID, review.Text, review.Song));
        }
    }

    public boolean addArtist(String username, String stageName){
        try {
            dbManager.addArtist(new Artist(username, null, null), stageName);
        }
        catch(PersistenceInconsistencyException e){
            return false;
        }
        return true;
    }

    public boolean deleteRequest(String username){
        return dbManager.deleteRequest(username);
    }

    public void showReports(){
        reportBox.setVisible(true);
        reportBox.setManaged(true);
        requestBox.setVisible(false);
        requestBox.setManaged(false);
    }

    public void showRequests(){
        reportBox.setVisible(false);
        reportBox.setManaged(false);
        requestBox.setVisible(true);
        requestBox.setManaged(true);
    }

    public void logout(MouseEvent mouseEvent) {
        LayoutManager manager = LayoutManagerFactory.getManager();
        manager.context.setAuthenticatedUser(null);
        manager.context.setAuthenticatedArtist(null);
        manager.context.inAdminPanel = false;
        try {
            manager.showAuthenticationPage(Path.LOGIN);
        } catch (IOException e) {
            // TODO: log
            e.printStackTrace();
        }
    }
}
