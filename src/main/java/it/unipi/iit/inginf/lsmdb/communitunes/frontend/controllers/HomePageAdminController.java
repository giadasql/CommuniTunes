package it.unipi.iit.inginf.lsmdb.communitunes.frontend.controllers;

import it.unipi.iit.inginf.lsmdb.communitunes.authentication.AuthenticationManager;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.*;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.ArtistPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.SongPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.components.ArtistPreviewVBox;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.components.SongPreviewVBox;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.LayoutManager;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.events.SongPreviewClickedEvent;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.Persistence;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.PersistenceFactory;
import it.unipi.iit.inginf.lsmdb.communitunes.utilities.exceptions.PersistenceInconsistencyException;
import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomePageAdminController implements UIController {

    public ChoiceBox searchMenu;
    public TextField searchText;
    public VBox reportBox;
    public VBox requestBox;
    public VBox searchBox;
    public Text message;

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
            AnchorPane reportPane = new AnchorPane();
            Button banButton = new Button("Ban");
            Button commentsButton = new Button("Comments");
            Button cancelButton = new Button("Cancel");
            TextField username = new TextField(report.reportedUser);
            TextField reportNumber = new TextField("Number of reports:   " + report.numReports);
            banButton.getStyleClass().add("admin-btn");
            commentsButton.getStyleClass().add("admin-btn");
            cancelButton.getStyleClass().add("admin-btn");
            banButton.setOnMouseClicked((event)-> {
                if(deleteUser(report.reportedUser)){
                    reportPane.setVisible(false);
                    reportPane.setManaged(false);
                    if(report.reportedUser.equals(searchText.getText()) && searchMenu.getValue().toString().equals("User")){
                        searchBox.getChildren().clear();
                    }
                }
            });

            commentsButton.setOnMouseClicked((event)->{
                showComments(report.reportedUser, report.comments);
            });
            cancelButton.setOnMouseClicked((event)->{
                if(deleteReport(report.reportedUser)){
                    reportPane.setVisible(false);
                    reportPane.setManaged(false);
                }
            });

            reportPane.setPrefHeight(95);
            reportPane.setPrefWidth(401);
            username.setLayoutX(27);
            username.setLayoutY(31);
            username.setFont(FONT);
            reportNumber.setLayoutX(27);
            reportNumber.setLayoutY(68);
            reportNumber.setFont(FONT);
            banButton.setPrefHeight(37);
            banButton.setPrefWidth(84);
            banButton.setLayoutX(310);
            banButton.setLayoutY(6);
            commentsButton.setPrefHeight(37);
            commentsButton.setPrefWidth(105);
            commentsButton.setLayoutX(207);
            commentsButton.setLayoutY(6);
            cancelButton.setPrefHeight(37);
            cancelButton.setPrefWidth(58);
            cancelButton.setLayoutX(149);
            cancelButton.setLayoutY(6);

            reportPane.getChildren().addAll(cancelButton, banButton, reportNumber, username, commentsButton);

            reportBox.getChildren().add(reportPane);
        }
        for(Request request : requestList){
            AnchorPane requestPane = new AnchorPane();
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

    public void showComments(String username, HashMap<String, String> comments){
        searchBox.getChildren().clear();
        for(Map.Entry<String, String> entry : comments.entrySet()){
            searchBox.getChildren().add(buildCommentPane(username, entry.getKey(), entry.getValue()));
        }
    }

    public boolean deleteReport(String username){
        return dbManager.deleteReport(username);
    }

    public boolean deleteUser(String username){
        return dbManager.deleteUser(new User(username, null, null));
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

    public AnchorPane buildCommentPane(String username, String commentId, String comment){
        AnchorPane pane = new AnchorPane();
        Button deleteButton = new Button("Delete");
        Text usernameField = new Text(username);
        Text commentField = new Text("Comment:   " + comment);
        deleteButton.getStyleClass().add("admin-btn");
        deleteButton.setOnMouseClicked((event)-> {
            if(dbManager.deleteComment(commentId)){
                pane.setVisible(false);
                pane.setManaged(false);
            }
        });

        pane.setPrefHeight(75);
        pane.setPrefWidth(401);
        usernameField.setLayoutX(0);
        usernameField.setLayoutY(21);
        usernameField.setFont(FONT);
        commentField.setLayoutX(79);
        commentField.setLayoutY(56);
        commentField.setFont(FONT);
        commentField.setStyle("-fx-text-fill: black");
        if(comment == null){
            commentField.setVisible(false);
            commentField.setManaged(false);
        }

        pane.getChildren().addAll(deleteButton, usernameField, commentField);

        return pane;
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
            AnchorPane commentPane = buildCommentPane(comment.User, comment.ID, comment.Text);
            searchBox.getChildren().add(commentPane);
        }
    }

    public void getReviewsByUsername(String username){
        List<Review> reviews = dbManager.getReviewsByUsername(username);
        for(Review comment : reviews){
            AnchorPane commentPane = buildCommentPane(comment.User, comment.ID, comment.Text);
            searchBox.getChildren().add(commentPane);
        }
    }

    public void printMissingValueMessage(){
        searchBox.getChildren().clear();
        message = new Text("There are missing fields, please compile them to search!");
        message.setStyle("-fx-text-fill: red");
        searchBox.getChildren().add(message);
    }
}
