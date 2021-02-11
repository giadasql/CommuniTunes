package it.unipi.iit.inginf.lsmdb.communitunes.entities.previews;

public class ArtistPreview extends UserPreview implements Preview{
    public ArtistPreview(String username, String image) {
        super(username, image);
    }

    @Override
    public String getIdentifier() {
        return this.username;
    }
}
