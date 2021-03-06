package it.unipi.iit.inginf.lsmdb.communitunes.frontend.components;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.ArtistPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.SongPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.components.previews.ArtistPreviewVBox;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.components.previews.SongPreviewVBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;

import java.util.List;

public class HomepageAnchorPane extends AnchorPane {
    private final Label genre;
    private final Line line;
    private final HBox genreHbox;
    private final List<SongPreview> songs;
    private final ArtistPreview artist;
    private final VBox layout;

    private static final Font FONT = new Font("Book Antiqua", 20.0);

    public HomepageAnchorPane(String genre, List<SongPreview> songs, ArtistPreview artist){
        this.genre = new Label(genre);
        this.songs = songs;
        this.artist = artist;
        this.genre.setFont(FONT);
        this.layout = new VBox();
        this.genre.setTextFill(Color.WHITE);
        genreHbox = new HBox();
        genreHbox.setPrefWidth(885.0);
        genreHbox.setPrefHeight(165.0);
        line = new Line();
        line.setStartX(874);
        line.setEndX(13);
        line.setStartY(30);
        line.setEndY(30);
        line.setStroke(Color.WHITE);
//
//        AnchorPane.setLeftAnchor(this.genre, 14.0);
//        AnchorPane.setTopAnchor(this.genre, 21.0);
//        AnchorPane.setLeftAnchor(line, 0.0);
//        AnchorPane.setTopAnchor(line, 0.0);
//        AnchorPane.setLeftAnchor(genreHbox, 8.0);
//        AnchorPane.setTopAnchor(genreHbox, 38.0);
        this.layout.getChildren().addAll(this.genre, line, genreHbox);
        getChildren().add(layout);

        genreHbox.setVisible(true);
        genreHbox.setManaged(true);

        if(artist != null){
            genreHbox.getChildren().add(new ArtistPreviewVBox(artist, null));
        }

        for(SongPreview song : songs){
            genreHbox.getChildren().add(new SongPreviewVBox(song, null));
        }
    }
}
