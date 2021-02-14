package it.unipi.iit.inginf.lsmdb.communitunes.frontend.controllers;

import it.unipi.iit.inginf.lsmdb.communitunes.authentication.AuthenticationManager;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.*;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.ArtistPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.SongPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.components.AdminReviewView;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.components.ArtistPreviewVBox;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.components.ReportView;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.components.SongPreviewVBox;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.LayoutManager;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.events.ShowUserReviewsEvent;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.events.SongPreviewClickedEvent;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.events.UserDeletedEvent;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.Persistence;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.PersistenceFactory;
import it.unipi.iit.inginf.lsmdb.communitunes.utilities.exceptions.PersistenceInconsistencyException;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.awt.*;
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
    public AnchorPane requestPane;

    public String showingCommentsOf;

    private Persistence dbManager;

    private static final Font FONT = new Font("Book Antiqua", 14.0);

    public void init(){
        dbManager = PersistenceFactory.CreatePersistence();
        ArrayList<String> choices = new ArrayList<>();
        choices.add("Song");
        choices.add("Comment");
        searchMenu.setItems(FXCollections.observableArrayList(choices));
        List<Report> reportList = dbManager.getReports();
        List<Request> requestList = dbManager.getRequests();
        for(Report report : reportList){
            reportBox.getChildren().add(new ReportView(report));
            reportBox.addEventHandler(ReportView.USER_DELETED_EVENT, this::userDeleted);
            reportBox.addEventHandler(ReportView.SHOW_USER_REVIEWS_EVENT, this::showComments);
        }

        for(Request request : requestList){
            requestPane = new AnchorPane();
            Button upgradeButton = new Button("Upgrade");
            Button cancelButton = new Button("Cancel");
            TextField username = new TextField(request.username);
            upgradeButton.getStyleClass().add("admin-btn");
            cancelButton.getStyleClass().add("admin-btn");
            upgradeButton.setOnMouseClicked((event)-> {
                if(addArtist(request.username, request.requestedStageName)) {
                    requestPane.setVisible(false);
                    requestPane.setManaged(false);
                }
            });
            cancelButton.setOnMouseClicked((event)->{
                if(deleteRequest(request.username)){
                    requestPane.setVisible(false);
                    requestPane.setManaged(false);
                }
            });

            requestPane.setPrefHeight(95);
            requestPane.setPrefWidth(401);
            username.setLayoutX(27);
            username.setLayoutY(31);
            username.setFont(FONT);
            upgradeButton.setPrefHeight(37);
            upgradeButton.setPrefWidth(96);
            upgradeButton.setLayoutX(205);
            upgradeButton.setLayoutY(6);
            cancelButton.setPrefHeight(37);
            cancelButton.setPrefWidth(96);
            cancelButton.setLayoutX(301);
            cancelButton.setLayoutY(6);

            requestPane.getChildren().addAll(cancelButton, upgradeButton, username);
            requestBox.getChildren().add(requestPane);
        }

        reportBox.setVisible(true);
        reportBox.setManaged(true);
        requestBox.setVisible(false);
        requestBox.setManaged(false);

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

    public boolean deleteReport(String username){
        return dbManager.deleteReport(username);
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
        reportBox.getParent().setVisible(true);
        reportBox.getParent().setManaged(true);
        requestBox.getParent().setVisible(false);
        requestBox.getParent().setManaged(false);
    }

    public void showRequests(){
        reportBox.getParent().setVisible(false);
        reportBox.getParent().setManaged(false);
        requestBox.getParent().setVisible(true);
        requestBox.getParent().setManaged(true);
    }

    public void searchItem(){
        if(searchMenu.getValue() == null || searchText.getText() == null){
            printMissingValueMessage();
            return;
        }
        else if(searchMenu.getValue().toString().equals("Song")){
            if(searchText.getText().equals("")){
                printMissingValueMessage();
                return;
            }
            searchBox.getChildren().clear();
            insertSongPreview(searchText.getText());
        }else if(searchMenu.getValue().toString().equals("Comment")){
            if(searchText.getText().equals("")){
                printMissingValueMessage();
                return;
            }
            searchBox.getChildren().clear();
            getReviewsByUsername(searchText.getText());
        }else{
            printMissingValueMessage();
            return;
        }
    }

    public void insertSongPreview(String title){
        List<SongPreview> songs = dbManager.getSongs(title);
        for(SongPreview song : songs){
            searchBox.getChildren().add(new SongPreviewVBox(song, this::retrieveAndShowComments));
        }
    }

    public void retrieveAndShowComments(SongPreviewClickedEvent event) {
        searchBox.getChildren().clear();
        List<Review> comments = dbManager.getReviews(event.preview.ID, 50);
        for(Review comment : comments){
            searchBox.getChildren().add(new AdminReviewView(comment.User, comment.ID, comment.Text, comment.Song));
        }
    }

    public void getReviewsByUsername(String username){
        List<Review> reviews = dbManager.getReviewsByUsername(username);
        for(Review comment : reviews){
            searchBox.getChildren().add(new AdminReviewView(comment.User, comment.ID, comment.Text, comment.Song));
        }
    }

    public void printMissingValueMessage(){
        searchBox.getChildren().clear();
        message = new Text("There are missing fields, please compile them to search!");
        message.setStyle("-fx-text-fill: red");
        searchBox.getChildren().add(message);
    }
}
