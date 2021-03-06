package it.unipi.iit.inginf.lsmdb.communitunes.frontend.controllers.user;

import it.unipi.iit.inginf.lsmdb.communitunes.authentication.AuthenticationFactory;
import it.unipi.iit.inginf.lsmdb.communitunes.authentication.AuthenticationManager;
import it.unipi.iit.inginf.lsmdb.communitunes.authentication.Role;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.User;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.SongPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.components.ListHbox;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.LayoutManager;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.LayoutManagerFactory;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.Path;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.controllers.UIController;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.Persistence;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.PersistenceFactory;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LikedSongsController implements UIController {

    public VBox mainVbox;
    public Button nextPageBtn;
    public Button prevPageBtn;
    public ScrollPane scrollPane;

    private User user;
    private Persistence dbManager;
    private AuthenticationManager authManager;
    private LayoutManager manager;

    private int startIndex = 0;
    private final int count = 54;


    @Override
    public void init(){
        manager = LayoutManagerFactory.getManager();
        user = manager.context.getFocusedUser();
        dbManager = manager.dbManager;
        authManager = AuthenticationFactory.CreateAuthenticationManager();

        List<SongPreview> followingArtists = dbManager.getLikedSongs(user.username, startIndex, count);
        prevPageBtn.setDisable(true);
        showPreviews(followingArtists);
    }

    public void showPreviews(List<SongPreview> toShow){
        scrollPane.setVvalue(0);
        mainVbox.getChildren().clear();
        if(toShow == null){
            return;
        }
        List<SongPreview> temp = new ArrayList<>();
        for(SongPreview artistPreview : toShow){
            temp.add(artistPreview);
            if(temp.size() == 6){
                mainVbox.getChildren().add(new ListHbox().buildSongList(temp));
                temp = new ArrayList<>();
            }
        }
        if(temp.size() > 0){
            mainVbox.getChildren().add(new ListHbox().buildSongList(temp));
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

    public void nextPage(ActionEvent actionEvent) {
        startIndex = startIndex + count;
        List<SongPreview> newPreviews = dbManager.getLikedSongs(user.username, startIndex, count);
        if(!newPreviews.isEmpty()){
            
            showPreviews(newPreviews);
            prevPageBtn.setDisable(false);
        }
        else{
            nextPageBtn.setDisable(true);
            startIndex = startIndex - count;
        }

    }

    public void prevPage(ActionEvent actionEvent) {
        if(startIndex >= count){
            startIndex = startIndex - count;
            List<SongPreview> newPreviews = dbManager.getLikedSongs(user.username, startIndex, count);
            showPreviews(newPreviews);
            nextPageBtn.setDisable(false);
        }
        if(startIndex == 0){
            prevPageBtn.setDisable(true);
        }
    }
}
