package it.unipi.iit.inginf.lsmdb.communitunes.frontend.components;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.ArtistPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.SongPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.UserPreview;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import java.util.List;

public class ListHbox extends HBox {

    public AnchorPane anchorOne;
    public AnchorPane anchorTwo;
    public HBox innerHboxOne;
    public HBox innerHboxTwo;

    public ListHbox(){
        anchorOne = new AnchorPane();
        anchorTwo = new AnchorPane();
        anchorOne.setPrefWidth(496.0);
        anchorOne.setPrefHeight(207.0);
        anchorTwo.setPrefWidth(496.0);
        anchorTwo.setPrefHeight(207.0);

        innerHboxOne = new HBox();
        innerHboxOne.setPrefWidth(378.0);
        innerHboxOne.setPrefHeight(165.0);
        innerHboxOne.setLayoutX(32);
        innerHboxOne.setLayoutY(36);

        innerHboxTwo = new HBox();
        innerHboxTwo.setPrefWidth(378.0);
        innerHboxTwo.setPrefHeight(165.0);
        innerHboxTwo.setLayoutX(32);
        innerHboxTwo.setLayoutY(36);

        getChildren().addAll(anchorOne, anchorTwo);
        innerHboxOne.setVisible(true);
        innerHboxOne.setManaged(true);
        anchorOne.getChildren().add(innerHboxOne);
        anchorTwo.getChildren().add(innerHboxTwo);
        innerHboxTwo.setVisible(true);
        innerHboxTwo.setManaged(true);
    }

    public HBox buildUserList(List<UserPreview> list){
        int counter = 1;
        for(UserPreview user : list){
            if(counter <= 3)
                innerHboxOne.getChildren().add(new UserPreviewVBox(user, null));
            else
                innerHboxTwo.getChildren().add(new UserPreviewVBox(user, null));
            counter++;
        }
        return this;
    }

    public HBox buildSongList(List<SongPreview> list){
        int counter = 1;
        for(SongPreview song : list){
            if(counter <= 3)
                innerHboxOne.getChildren().add(new SongPreviewVBox(song, null));
            else
                innerHboxTwo.getChildren().add(new SongPreviewVBox(song, null));
            counter++;
        }
        return this;
    }

    public HBox buildArtistList(List<ArtistPreview> list){
        int counter = 1;
        for(ArtistPreview artist : list){
            if(counter <= 3)
                innerHboxOne.getChildren().add(new ArtistPreviewVBox(artist, null));
            else
                innerHboxTwo.getChildren().add(new ArtistPreviewVBox(artist, null));
            counter++;
        }
        return this;
    }
}
