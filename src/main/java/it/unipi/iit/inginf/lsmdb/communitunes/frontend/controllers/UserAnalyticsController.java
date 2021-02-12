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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import org.javatuples.Pair;

import java.io.IOException;
import java.util.List;

public class UserAnalyticsController implements UIController {

    public HBox suggestedArtists;
    public HBox suggestedUsers;
    public HBox likemindedUsers;
    public HBox suggestedSongs;
    public HBox likemindedSongs;

    private User user;
    private LayoutManager manager;
    private Persistence dbManager;

    public void init() {
        this.manager = LayoutManagerFactory.getManager();
        dbManager = PersistenceFactory.CreatePersistence();
        user = manager.context.getAuthenticatedUser();
        List<ArtistPreview> suggestedArtistsList = dbManager.getSuggestedArtists(user);
        System.out.println("1");
        List<UserPreview> suggestedUsersList = dbManager.getSuggestedUsers(user);
        System.out.println("2");
        Pair<List<UserPreview>, List<SongPreview>> likeMinded = dbManager.getLikeMindedUsers(user);
        List<UserPreview> likemindedUsersList = likeMinded.getValue0();
        List<SongPreview> likemindedSongsList = likeMinded.getValue1();
        System.out.println("3");
        List<SongPreview> suggestedSongsList = dbManager.getSuggestedSongs(user);
        System.out.println("4");
        List<ArtistPreview> coworkers = dbManager.getCoworkersOfFollowedArtists(user);

        for(ArtistPreview artist : suggestedArtistsList){
            suggestedArtists.getChildren().add(new ArtistPreviewVBox(artist, null));
        }
        if(suggestedArtists.getChildren().isEmpty()){
            suggestedArtists.getParent().setVisible(false);
            suggestedArtists.getParent().setManaged(false);
        }

        for(UserPreview preview : suggestedUsersList){
            suggestedUsers.getChildren().add(new UserPreviewVBox(preview, null));
        }
        if(suggestedUsers.getChildren().isEmpty()){
            suggestedUsers.getParent().setVisible(false);
            suggestedUsers.getParent().setManaged(false);
        }

        for(UserPreview preview : likemindedUsersList){
            likemindedUsers.getChildren().add(new UserPreviewVBox(preview, null));
        }
        if(likemindedUsers.getChildren().isEmpty()){
            likemindedUsers.getParent().setVisible(false);
            likemindedUsers.getParent().setManaged(false);
        }

        for(SongPreview song : suggestedSongsList){
            suggestedSongs.getChildren().add(new SongPreviewVBox(song, null));
        }
        if(suggestedSongs.getChildren().isEmpty()){
            suggestedSongs.getParent().setVisible(false);
            suggestedSongs.getParent().setManaged(false);
        }

        for(SongPreview song : likemindedSongsList){
            likemindedSongs.getChildren().add(new SongPreviewVBox(song, null));
        }
        if(likemindedSongs.getChildren().isEmpty()){
            likemindedSongs.getParent().setVisible(false);
            likemindedSongs.getParent().setManaged(false);
        }

    }

    public void closeWindow(MouseEvent mouseEvent) throws IOException {
        manager.setContent(Path.USER_PROFILE);
    }
}
