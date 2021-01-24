package it.unipi.iit.inginf.lsmdb.communitunes.entities.previews;

public class ArtistPreview extends UserPreview {
    public ArtistPreview(String username, String stageName, String image) {
        super(username);
        this.username = username;
        this.stageName = stageName;
        this.image = image;
    }

    public String stageName;
    public String image;
}
