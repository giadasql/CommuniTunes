package it.unipi.iit.inginf.lsmdb.communitunes.frontend.controllers.artist;

import it.unipi.iit.inginf.lsmdb.communitunes.authentication.Role;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.Artist;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.ArtistPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.SongPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.UserPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.components.previews.ArtistPreviewVBox;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.components.previews.SongPreviewVBox;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.components.previews.UserPreviewVBox;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.LayoutManager;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.LayoutManagerFactory;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.Path;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.controllers.UIController;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.Persistence;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.PersistenceFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import org.javatuples.Pair;

import java.io.IOException;
import java.util.List;

public class ArtistAnalytics implements UIController {

    public HBox similarArtists;
    public HBox topFans;
    public HBox popularSongs;
    public HBox suggestedArtists;
    public HBox suggestedUsers;
    public HBox suggestedSongs;
    public HBox likemindedUsers;
    public HBox likemindedSongs;
    public HBox coworkersHbox;

    private Artist artist;
    private LayoutManager manager;
    private Persistence dbManager;

    public void init() {
        this.manager = LayoutManagerFactory.getManager();
        dbManager = PersistenceFactory.CreatePersistence();
        artist = manager.context.getAuthenticatedArtist();
        List<ArtistPreview> similarArtistsList = dbManager.getSimilarArtists(artist);
        List<UserPreview> topFansList = dbManager.getTopFans(artist);
        List<SongPreview> popularSongsList = dbManager.getPopularSongs(artist);

        for(ArtistPreview artist : similarArtistsList){
            similarArtists.getChildren().add(new ArtistPreviewVBox(artist, null));
        }
        if(similarArtists.getChildren().isEmpty()){
            similarArtists.getParent().setVisible(false);
            similarArtists.getParent().setManaged(false);
        }

        for(UserPreview preview : topFansList){
            topFans.getChildren().add(new UserPreviewVBox(preview, null));
        }
        if(topFans.getChildren().isEmpty()){
            topFans.getParent().setVisible(false);
            topFans.getParent().setManaged(false);
        }

        for(SongPreview song : popularSongsList){
            popularSongs.getChildren().add(new SongPreviewVBox(song, null));
        }
        if(popularSongs.getChildren().isEmpty()){
            popularSongs.getParent().setVisible(false);
            popularSongs.getParent().setManaged(false);
        }

        List<ArtistPreview> suggestedArtistsList = dbManager.getArtistsFollowedByFriends(artist);
        List<UserPreview> suggestedUsersList = dbManager.getUsersFollowedByFriends(artist);
        Pair<List<UserPreview>, List<SongPreview>> likeMinded = dbManager.getLikeMindedUsersAndTheSongsTheyLike(artist);
        List<UserPreview> likemindedUsersList = likeMinded.getValue0();
        List<SongPreview> likemindedSongsList = likeMinded.getValue1();
        List<SongPreview> suggestedSongsList = dbManager.getFollowedUsersLikedSongs(artist);
        List<ArtistPreview> coworkers = dbManager.getCoworkersOfFollowedArtists(artist);


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

        for(ArtistPreview artist : coworkers){
            coworkersHbox.getChildren().add(new ArtistPreviewVBox(artist, null));
        }
        if(coworkersHbox.getChildren().isEmpty()){
            coworkersHbox.getParent().setVisible(false);
            coworkersHbox.getParent().setManaged(false);
        }
    }

    public void closeWindow(MouseEvent mouseEvent) throws IOException {
        if(manager.context.getFocusedRole() == Role.Artist){
            manager.setContent(Path.ARTIST_PROFILE);
        }
        else{
            manager.setContent(Path.USER_PROFILE);
        }
    }
}
