package it.unipi.iit.inginf.lsmdb.communitunes.entities.previews;

public class UserPreview implements Preview {

    public UserPreview(String username, String image) {
        this.username = username;
        this.image = image;
    }

    public String username;
    public String image;

    @Override
    public String getIdentifier() {
        return this.username;
    }
}
