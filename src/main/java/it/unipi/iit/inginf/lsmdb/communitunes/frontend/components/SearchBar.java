package it.unipi.iit.inginf.lsmdb.communitunes.frontend.components;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.Review;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.ArtistPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.SongPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.UserPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.events.FoundArtistsEvent;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.events.FoundReviewsEvent;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.events.FoundSongsEvent;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.events.FoundUsersEvent;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.Persistence;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.PersistenceFactory;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchBar extends HBox {
    private final ChoiceBox<String> choiceBox;
    private final TextField textField;

    public SearchBar() {
        Text searchFor = new Text("Search: ");
        searchFor.setFont(new Font("System", 18));
        searchFor.setFill(Color.WHITE);

        choiceBox = new ChoiceBox<String>();
        ArrayList<String> choices = new ArrayList<>();
        choices.add("Songs");
        choices.add("Users (Username)");
        choices.add("Artists (Username)");
        choices.add("Stage Name");
        choices.add("Reviews");
        choiceBox.setItems(FXCollections.observableArrayList(choices));
        choiceBox.setValue("Songs");

        textField = new TextField();
        textField.prefWidth(136);

        Button searchBtn = new Button();
        searchBtn.setCursor(Cursor.HAND);
        searchBtn.setMaxHeight(35);
        searchBtn.setMaxWidth(35);
        searchBtn.setBackground(Background.EMPTY);
        ImageView imageView = new ImageView(new Image(this.getClass().getResourceAsStream("/ui/img/magnifier.png")));
        searchBtn.setGraphic(imageView);
        searchBtn.setOnMouseClicked(this::search);
        textField.setOnKeyPressed(this::search);
        this.getChildren().addAll(Arrays.asList(searchFor, choiceBox, textField, searchBtn));
    }

    private void search(KeyEvent keyEvent) {
        if(keyEvent.getCode().equals(KeyCode.ENTER)){
            sendResults();
        }
    }

    private void sendResults() {
        Persistence dbManager = PersistenceFactory.CreatePersistence();
        if("Stage Name".equals(choiceBox.getValue())){
            List<ArtistPreview> foundArtists = dbManager.searchArtistByName(textField.getText(), 0, 15);
            FoundArtistsEvent foundArtistsEvent = new FoundArtistsEvent(FOUND_ARTISTS_EVENT,foundArtists);
            fireEvent(foundArtistsEvent);
        }
        else if("Songs".equals(choiceBox.getValue())){
            List<SongPreview> foundSongs = dbManager.searchSongByTitle(textField.getText(), 0, 15);
            FoundSongsEvent foundSongsEvent = new FoundSongsEvent(FOUND_SONGS_EVENT, foundSongs);
            fireEvent(foundSongsEvent);
        }
        else if("Users (Username)".equals(choiceBox.getValue())){
            List<UserPreview> foundUsers = dbManager.searchUserByUsername(textField.getText(), 0, 15);
            FoundUsersEvent foundUsersEvent = new FoundUsersEvent(FOUND_USERS_EVENT, foundUsers);
            fireEvent(foundUsersEvent);
        }
        else if("Artists (Username)".equals(choiceBox.getValue())){
            List<ArtistPreview> foundArtists = dbManager.searchArtistsByUsername(textField.getText(), 0, 15);
            FoundArtistsEvent foundArtistsEvent = new FoundArtistsEvent(FOUND_ARTISTS_EVENT,foundArtists);
            fireEvent(foundArtistsEvent);
        }
        else if("Reviews".equals(choiceBox.getValue())){
            List<Review> foundReviews = dbManager.getReviewsByUsername(textField.getText());
            FoundReviewsEvent foundReviewsEvent = new FoundReviewsEvent(FOUND_REVIEWS_EVENT, foundReviews, textField.getText());
            fireEvent(foundReviewsEvent);
        }
    }

    public static final EventType<Event> SEARCH_EVENT = new EventType<>(Event.ANY, "SEARCH_EVENT");
    public static final EventType<FoundUsersEvent> FOUND_USERS_EVENT = new EventType<>(SEARCH_EVENT, "FOUND_USERS_EVENT");
    public static final EventType<FoundSongsEvent> FOUND_SONGS_EVENT = new EventType<>(SEARCH_EVENT, "FOUND_SONGS_EVENT");
    public static final EventType<FoundArtistsEvent> FOUND_ARTISTS_EVENT = new EventType<>(SEARCH_EVENT, "FOUND_ARTISTS_EVENT");
    public static final EventType<FoundReviewsEvent> FOUND_REVIEWS_EVENT = new EventType<>(SEARCH_EVENT, "FOUND_REVIEWS_EVENT");

    private void search(MouseEvent mouseEvent) {
        sendResults();
    }
}
