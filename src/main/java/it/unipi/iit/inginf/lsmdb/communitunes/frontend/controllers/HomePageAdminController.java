package it.unipi.iit.inginf.lsmdb.communitunes.frontend.controllers;

import it.unipi.iit.inginf.lsmdb.communitunes.authentication.AuthenticationManager;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.Artist;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.Report;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.Request;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.User;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.ArtistPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.components.ArtistPreviewVBox;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.LayoutManager;
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
    public TextField message;

    private Persistence dbManager;

    private static final Font FONT = new Font("Book Antiqua", 20.0);

    public void init(){
        dbManager = PersistenceFactory.CreatePersistence();
        ArrayList<String> choices = new ArrayList<>();
        choices.add("User");
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

        reportBox.getParent().setVisible(true);
        reportBox.getParent().setManaged(true);
        requestBox.getParent().setVisible(false);
        requestBox.getParent().setManaged(false);

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
        TextField usernameField = new TextField(username);
        TextField commentField = new TextField("Comment:   " + comment);
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

        pane.getChildren().addAll(deleteButton, usernameField, commentField);

        return pane;
    }

    public void searchItem(){
        if(searchMenu.getValue() == null || searchText.getText() == null){
            printMissingValueMessage();
            return;
        }
        else if(searchMenu.getValue().toString() == "Song"){
            if(searchText.getText() == ""){
                printMissingValueMessage();
                return;
            }
            searchBox.getChildren().clear();

        }else if(searchMenu.getValue().toString() == "Comment"){
            if(searchText.getText() == ""){
                printMissingValueMessage();
                return;
            }
            searchBox.getChildren().clear();


        }else{
            printMissingValueMessage();
            return;
        }
    }

    public void printMissingValueMessage(){
        searchBox.getChildren().clear();
        message.setText("There are missing fields, please compile them to search!");
        message.setStyle("-fx-text-fill: red");
    }
}
