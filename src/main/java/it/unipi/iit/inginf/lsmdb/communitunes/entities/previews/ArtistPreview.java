package it.unipi.iit.inginf.lsmdb.communitunes.entities.previews;

public class ArtistPreview extends UserPreview {
    public ArtistPreview(String username, String stageName, String image) {
        super(username, image);
        this.stageName = stageName;
    }
    public String stageName;

}
