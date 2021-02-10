package it.unipi.iit.inginf.lsmdb.communitunes.persistence;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.*;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.ArtistPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.SongPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.UserPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.utilities.configurations.ConfigReader;
import it.unipi.iit.inginf.lsmdb.communitunes.utilities.configurations.ConfigReaderFactory;
import it.unipi.iit.inginf.lsmdb.communitunes.utilities.configurations.ConfigReaderType;
import it.unipi.iit.inginf.lsmdb.communitunes.utilities.exceptions.PersistenceInconsistencyException;
import org.javatuples.Pair;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;

class PersistenceImplementation implements Persistence {

    private ConfigReader reader;

    private MongoDriver mongo;
    private Neo4jDriver neo4j;

    private final List<String> SongConstructorParameters = new ArrayList<>(Arrays.asList("mainArtist", "duration", "title", "image", "album", "loadedReviews", "links", "genres", "featurings", "id"));

    public PersistenceImplementation() {
        String settingsFileName = "Settings.xml";
        InputStream settingsFileStream = this.getClass().getClassLoader().getResourceAsStream(settingsFileName);
        try{
            reader = ConfigReaderFactory.CreateConfigReader(ConfigReaderType.Xml, settingsFileStream);
        }
        catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
            // TODO: log
            return;
        }
        if(reader == null){
            // TODO: log
            return;
        }

        neo4jInit();
        mongoInit();
    }

    private void neo4jInit(){
        String neo4jUri = reader.getStringConfigValue("Neo4j", "uri");
        String neo4jUser = reader.getStringConfigValue("Neo4j", "username");
        String neo4jPsw = reader.getStringConfigValue("Neo4j", "password");
        neo4j = new Neo4jDriver(neo4jUri, neo4jUser, neo4jPsw);
    }

    private void mongoInit(){
        String connectionString = reader.getStringConfigValue("MongoDB", "connectionString");
        mongo = new MongoDriver(connectionString);
    }

    public boolean checkIfUsernameExists(String username){
        return mongo.checkIfUsernameExists(username) || neo4j.checkIfUsernameExists(username);
    }

    public boolean checkIfEmailExists(String email){
        return mongo.checkIfEmailExists(email);
    }

    public boolean addNewUser(User newUser) throws PersistenceInconsistencyException {
        String mongoID = mongo.addUser(newUser.Username, newUser.Email, newUser.Password);
        if(mongoID != null){
            int neoID = neo4j.addUser(newUser.Username);
            if(neoID != -1){
                return true;
            }
            else{
                if(mongo.deleteUser(newUser.Username)){
                    return false;
                }
                else{
                    throw new PersistenceInconsistencyException("User " + newUser.Username + " was not correctly added, but due to unexpected errors the user might be present in the database. This my cause inconsistencies.");
                }
            }
        }
        else{
            return false;
        }
    }

    public boolean deleteUser(User user){
        boolean neo4jDelete = neo4j.deleteUser(user.Username);
        boolean mongoDelete = mongo.deleteUser(user.Username);
        return (mongoDelete && neo4jDelete && deleteReviews(user.Username));
    }

    @Override
    public boolean updateUser(User newUser) {
        return mongo.updateUser(newUser) && neo4j.updateUser(newUser.Username, newUser.Image);
    }

    @Override
    public boolean addArtist(Artist newArtist, String stageName) throws PersistenceInconsistencyException {
        newArtist.StageName = stageName;
        String mongoID = mongo.addArtist(newArtist.Username, stageName);
        if(mongoID != null){
            int neoID = neo4j.addArtist(newArtist.Username, newArtist.StageName);
            if(neoID != -1){
                return true;
            }
            else{
                if(mongo.deleteArtist(newArtist.Username)){
                    return false;
                }
                else{
                    throw new PersistenceInconsistencyException("Artist " + newArtist.Username + " was not correctly added, but due to unexpected errors the artist might be present in the database, causing an inconsistency.");
                }
            }
        }
        else{
            return false;
        }
    }

    @Override
    public boolean deleteArtist(Artist artist) {
        boolean neo4jDelete = neo4j.deleteArtist(artist.Username);
        boolean mongoDelete = mongo.deleteArtist(artist.Username);
        return (mongoDelete && neo4jDelete && deleteReviews(artist.Username));
    }

    @Override
    public boolean updateArtist(Artist newArtist) {
        /*
        Artist toUpdate = getArtist(newArtist.Username);
        if(toUpdate == null || newArtist.equals(toUpdate)){
            return false;
        }


            Before contacting Neo4j, we check that the stage name of the artist, the only thing
            beside the username we save about the artist, is still the same or not, in order to
            avoid overhead for a communication that won't really change anything.

        boolean check = toUpdate.StageName == newArtist.StageName;

        toUpdate.Birthday = newArtist.Birthday;
        toUpdate.Country = newArtist.Country;
        toUpdate.Email = newArtist.Email;
        toUpdate.Password = newArtist.Password;
        toUpdate.StageName = newArtist.StageName;
        toUpdate.Image = newArtist.Image;
        toUpdate.ActiveYears = newArtist.ActiveYears;
        toUpdate.Biography = newArtist.Biography;

         */

        return mongo.updateArtist(newArtist) && neo4j.updateArtist(newArtist.Username, newArtist.StageName, newArtist.Image);
    }

        // TODO: Forse conviene passare l'artista insieme alla canzone?
    @Override
    public boolean addSong(Song newSong) throws PersistenceInconsistencyException {
        String mongoID = mongo.addSong(newSong.Artist.username, newSong.Duration, newSong.Title, newSong.Album);
        if(mongoID != null){
            int neoID = neo4j.addSong(newSong.Artist.username, newSong.Title, newSong.ID);
            if(neoID != -1){
                return true;
            }
            else{
                if(mongo.deleteSong(newSong.ID)){
                    return false;
                }
                else{
                    throw new PersistenceInconsistencyException("Song " + newSong.Title + " was not correctly added, but due to unexpected errors the song might be present in the database, causing an inconsistency.");
                }
            }
        }
        else{
            return false;
        }
    }

    @Override
    public boolean deleteSong(Song song) {
        boolean neo4jDelete = neo4j.deleteSong(song.Artist.username, song.Title);
        boolean mongoDelete = mongo.deleteSong(song.ID);
        return (mongoDelete && neo4jDelete);
    }

    @Override
    public boolean editSong(Song newSong) {

        /*Song toUpdate = getSong(newSong.ID);
        if(toUpdate == null || newSong.equals(toUpdate)){
            return false;
        }


            Before contacting Neo4j, we check if th list of featurings , in order to
            avoid overhead for a communication that won't really change anything.

        boolean check = toUpdate.Featurings.equals(newSong.Featurings);

        toUpdate.Link = newSong.Link;
        toUpdate.Image = newSong.Image;
        toUpdate.Genres.addAll(newSong.Genres);
         */

        return mongo.updateSong(newSong) && neo4j.updateSong(newSong.Title, newSong.Artist.username, newSong.Featurings, newSong.Image);
    }

    @Override
    public boolean addReview(Review review) {
        String insertedId = mongo.addReview(review.User, review.Rating, review.Text, review.Song);
        if(insertedId != null){
            review.ID = insertedId;
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteReview(Review review) {
        return mongo.deleteReview(review.Song, review.ID);
    }

    @Override
    public boolean editReview(Review newReview) {
        return mongo.updateReview(newReview.ID, newReview.Rating, newReview.Text);
    }

    @Override
    public List<SongPreview> getSuggestedSongs(User user) {
        return neo4j.getSuggestedSongs(user.Username);
    }

    @Override
    public HashMap<String, String> getApprAlbum(Artist artist){
        return mongo.getApprAlbum(artist.Username);
    }

    @Override
    public List<Pair<String,ArtistPreview>> getRepresentativeArtist(){
        return mongo.getRepresentativeArtist();
    }

    @Override
    public HashMap<String, List<SongPreview>> getSuggestedSongs() {
        return mongo.getSuggestedSongs();
    }

    @Override
    public List<ArtistPreview> getSuggestedArtists(User user) {
        return neo4j.getSuggestedArtists(user.Username);
    }

    @Override
    public List<UserPreview> getSuggestedUsers(User user) {
        return neo4j.getSuggestedUsers(user.Username);
    }

    @Override
    public List<UserPreview> getLikeMindedUsers(User user){
        return neo4j.getLikeMindedUsers(user.Username);
    }

    @Override
    public List<SongPreview> getLikeMindedSongs(User user){
        return neo4j.getLikeMindedSongs(user.Username);
    }

    @Override
    public List<UserPreview> getTopFans(Artist artist){
        return neo4j.getTopFans(artist.Username);
    }

    @Override
    public List<ArtistPreview> getSimilarArtists(Artist artist){ return neo4j.getSimilarArtists(artist.Username); }

    @Override
    public List<SongPreview> getPopularSongs(Artist artist) { return neo4j.getPopularSongs(artist.Username); }

    @Override
    public boolean addFollow(User followed, User follower) {
        return neo4j.addFollow(followed.Username, follower.Username);
    }

    @Override
    public boolean checkFollow(User followed, User follower) {
        return neo4j.checkFollow(followed.Username, follower.Username);
    }

    @Override
    public boolean deleteFollow(User followed, User follower) {
        return neo4j.deleteFollow(followed.Username, follower.Username);
    }

    @Override
    public boolean addLike(User user, Song song) {
        return neo4j.addLike(user.Username, song.ID);
    }

    @Override
    public boolean checkLike(User user, Song song) {
        return neo4j.checkLike(user.Username, song.ID);
    }

    @Override
    public boolean deleteLike(User user, Song song) {
        return neo4j.deleteLike(user.Username, song.ID);
    }

    @Override
    public boolean checkIfUserReviewedSong(User user, Song song) {
        return mongo.checkIfUserReviewedSong(user.Username, song.ID);
    }

    @Override
    public boolean deleteReviews(String username) {
        return mongo.deleteReviews(username);
    }

    @Override
    public int checkCredentials(String username, String password) {
        return mongo.checkCredentials(username, password);
    }

    @Override
    public User getUser(String username) {
        Map<String, Object> userData = mongo.getUserData(username);
        if(userData != null){
            userData.putAll(neo4j.getUserData(username));
            userData.put("username", username);
            return buildUserFromMap(userData);
        }
        return null;
    }

    @Override
    public Artist getArtist(String username) {
        Map<String, Object> artistData = mongo.getArtistData(username);
        if(artistData != null){
            artistData.putAll(neo4j.getArtistData(username));
            artistData.put("username", username);
            return buildArtistFromMap(artistData);
        }
        return null;
    }

    @Override
    public Song getSong(String songID) {
        Map<String, Object> songData = mongo.getSongData(songID);
        if(songData != null){
            songData.putAll(neo4j.getSongData(songID));
            songData.put("id", songID);
            return buildSongFromMap(songData);
        }
        return null;
    }

    @Override
    public List<Review> getReviews(String songID, int nMax) {
        List<Review> toReturn = new ArrayList<>();
        List<Map<String, Object>> reviewsData = new ArrayList<>(mongo.getSongReviews(songID, nMax));
        for (Map<String, Object> reviewData:
             reviewsData) {
            toReturn.add(buildReviewFromMap(reviewData));
        }

        return toReturn;
    }

    private User buildUserFromMap(Map<String, Object> userData){
        String email, password, country, image, firstName, lastName, username, id;
        LocalDate birthday;
        ArrayList<ArtistPreview> artistFollowers = new ArrayList<>();
        ArrayList<ArtistPreview> artistFollowed = new ArrayList<>();
        ArrayList<SongPreview> likes = new ArrayList<>();
        ArrayList<UserPreview> followed = new ArrayList<>();
        ArrayList<UserPreview> followers = new ArrayList<>();

        email = (userData.get("email") instanceof String ? (String)userData.get("email") : null);
        password = (userData.get("password") instanceof String ? (String)userData.get("password") : null);
        country = (userData.get("country") instanceof String ? (String)userData.get("country") : null);
        image = (userData.get("image") instanceof String ? (String)userData.get("image") : null);
        firstName = (userData.get("firstName") instanceof String ? (String)userData.get("firstName") : null);
        lastName = (userData.get("lastName") instanceof String ? (String)userData.get("lastName") : null);
        username = (userData.get("username") instanceof String ? (String)userData.get("username") : null);
        birthday = (userData.get("birthday") instanceof LocalDate ? (LocalDate) userData.get("birthday") : null);
        id = (userData.get("id") instanceof String ? (String) userData.get("id") : null);
        
        Iterable<Map<String,Object>> followedArtistsList = (Iterable<Map<String,Object>>)userData.get("followedArtists");
        for (Map<String,Object> artistMap : followedArtistsList){
            String artistUsername = (artistMap.get("username") instanceof String ? (String)artistMap.get("username") : null);
            String artistImage = (artistMap.get("image") instanceof String ? (String)artistMap.get("image") : null);
            String artistStageName = (artistMap.get("stageName") instanceof String ? (String)artistMap.get("stageName") : null);
            artistFollowed.add(new ArtistPreview(artistUsername, artistStageName, artistImage));
        }

        Iterable<Map<String,Object>> followerArtistsList = (Iterable<Map<String,Object>>)userData.get("followerArtists");
        for (Map<String,Object> artistMap : followerArtistsList){
            String artistUsername = (artistMap.get("username") instanceof String ? (String)artistMap.get("username") : null);
            String artistImage = (artistMap.get("image") instanceof String ? (String)artistMap.get("image") : null);
            String artistStageName = (artistMap.get("stageName") instanceof String ? (String)artistMap.get("stageName") : null);
            artistFollowers.add(new ArtistPreview(artistUsername, artistStageName, artistImage));
        }

        Iterable<Map<String,Object>> likesList = (Iterable<Map<String,Object>>)userData.get("likes");
        for (Map<String,Object> likeMap : likesList){
            String songID = (likeMap.get("id") instanceof String ? (String)likeMap.get("id") : null);
            String title = (likeMap.get("title") instanceof String ? (String)likeMap.get("title") : null);
            String artistUsername = (likeMap.get("artist") instanceof String ? (String)likeMap.get("artist") : null);
            String songImage = (likeMap.get("image") instanceof String ? (String)likeMap.get("image") : null);
            likes.add(new SongPreview(songID, artistUsername, title, songImage));
        }

        Iterable<Map<String,Object>> followedList = (Iterable<Map<String,Object>>)userData.get("followed");
        for (Map<String,Object> userMap : followedList){
            String userUsername = (userMap.get("username") instanceof String ? (String)userMap.get("username") : null);
            String userImage = (userMap.get("image") instanceof String ? (String)userMap.get("image") : null);
            followed.add(new UserPreview(userUsername, userImage));
        }

        Iterable<Map<String,Object>> followersList = (Iterable<Map<String,Object>>)userData.get("followers");
        for (Map<String,Object> userMap : followersList){
            String userUsername = (userMap.get("username") instanceof String ? (String)userMap.get("username") : null);
            String userImage = (userMap.get("image") instanceof String ? (String)userMap.get("image") : null);
            followers.add(new UserPreview(userUsername, userImage));
        }
        return new User(id, email, username, password, country, image, birthday, firstName, lastName, likes, followed, artistFollowed, followers, artistFollowers);
    }

    private Artist buildArtistFromMap( Map<String, Object> artistData){
        String email, password, country, image, firstName, lastName, username, id, stageName, biography, yearsActive;
        LocalDate birthday;
        ArrayList<ArtistPreview> artistFollowers = new ArrayList<>();
        ArrayList<ArtistPreview> artistFollowed = new ArrayList<>();
        ArrayList<SongPreview> likes = new ArrayList<>();
        ArrayList<SongPreview> songs = new ArrayList<>();
        ArrayList<UserPreview> followed = new ArrayList<>();
        ArrayList<UserPreview> followers = new ArrayList<>();
        ArrayList<Link> links = new ArrayList<>();

        email = (artistData.get("email") instanceof String ? (String)artistData.get("email") : null);
        password = (artistData.get("password") instanceof String ? (String)artistData.get("password") : null);
        country = (artistData.get("country") instanceof String ? (String)artistData.get("country") : null);
        image = (artistData.get("image") instanceof String ? (String)artistData.get("image") : null);
        firstName = (artistData.get("firstName") instanceof String ? (String)artistData.get("firstName") : null);
        lastName = (artistData.get("lastName") instanceof String ? (String)artistData.get("lastName") : null);
        username = (artistData.get("username") instanceof String ? (String)artistData.get("username") : null);
        birthday = (artistData.get("birthday") instanceof LocalDate ? (LocalDate) artistData.get("birthday") : null);
        id = (artistData.get("id") instanceof String ? (String) artistData.get("id") : null);
        stageName = (artistData.get("stageName") instanceof String ? (String) artistData.get("stageName") : null);
        biography = (artistData.get("biography") instanceof String ? (String) artistData.get("biography") : null);
        yearsActive = (artistData.get("activity") instanceof String ? (String) artistData.get("activity") : null);

        Iterable<Map<String,Object>> followedArtistsList = (Iterable<Map<String,Object>>)artistData.get("followedArtists");
        for (Map<String,Object> artistMap : followedArtistsList){
            String artistUsername = (artistMap.get("username") instanceof String ? (String)artistMap.get("username") : null);
            String artistImage = (artistMap.get("image") instanceof String ? (String)artistMap.get("image") : null);
            String artistStageName = (artistMap.get("stageName") instanceof String ? (String)artistMap.get("stageName") : null);
            artistFollowed.add(new ArtistPreview(artistUsername, artistStageName, artistImage));
        }

        Iterable<Map<String,Object>> followerArtistsList = (Iterable<Map<String,Object>>)artistData.get("followerArtists");
        for (Map<String,Object> artistMap : followerArtistsList){
            String artistUsername = (artistMap.get("username") instanceof String ? (String)artistMap.get("username") : null);
            String artistImage = (artistMap.get("image") instanceof String ? (String)artistMap.get("image") : null);
            String artistStageName = (artistMap.get("stageName") instanceof String ? (String)artistMap.get("stageName") : null);
            artistFollowers.add(new ArtistPreview(artistUsername, artistStageName, artistImage));
        }

        Iterable<Map<String,Object>> likesList = (Iterable<Map<String,Object>>)artistData.get("likes");
        for (Map<String,Object> likeMap : likesList){
            String songID = (likeMap.get("id") instanceof String ? (String)likeMap.get("id") : null);
            String title = (likeMap.get("title") instanceof String ? (String)likeMap.get("title") : null);
            String artistUsername = (likeMap.get("artist") instanceof String ? (String)likeMap.get("artist") : null);
            String songImage = (likeMap.get("image") instanceof String ? (String)likeMap.get("image") : null);
            likes.add(new SongPreview(songID, artistUsername, title, songImage));
        }

        Iterable<Map<String,Object>> songsList = (Iterable<Map<String,Object>>)artistData.get("songs");
        for (Map<String,Object> songMap : songsList){
            String songID = (songMap.get("id") instanceof String ? (String)songMap.get("id") : null);
            String title = (songMap.get("title") instanceof String ? (String)songMap.get("title") : null);
            String artistUsername = (songMap.get("artist") instanceof String ? (String)songMap.get("artist") : null);
            String songImage = (songMap.get("image") instanceof String ? (String)songMap.get("image") : null);
            songs.add(new SongPreview(songID, artistUsername, title, songImage));
        }

        Iterable<Map<String,Object>> followedList = (Iterable<Map<String,Object>>)artistData.get("followed");
        for (Map<String,Object> userMap : followedList){
            String userUsername = (userMap.get("username") instanceof String ? (String)userMap.get("username") : null);
            String userImage = (userMap.get("image") instanceof String ? (String)userMap.get("image") : null);
            followed.add(new UserPreview(userUsername, userImage));
        }

        Iterable<Map<String,Object>> linksList = (Iterable<Map<String,Object>>)artistData.get("links");
        for (Map<String,Object> linkMap : linksList){
            String linkName = (linkMap.get("name") instanceof String ? (String)linkMap.get("name") : null);
            String linkUrl = (linkMap.get("link") instanceof String ? (String)linkMap.get("link") : null);
            links.add(new Link(linkName, linkUrl));
        }

        Iterable<Map<String,Object>> followersList = (Iterable<Map<String,Object>>)artistData.get("followers");
        for (Map<String,Object> userMap : followersList){
            String userUsername = (userMap.get("username") instanceof String ? (String)userMap.get("username") : null);
            String userImage = (userMap.get("image") instanceof String ? (String)userMap.get("image") : null);
            followers.add(new UserPreview(userUsername, userImage));
        }
        return new Artist(id, email, username, password, country, image, birthday, firstName, lastName, likes, followed, artistFollowed, followers, artistFollowers, stageName, yearsActive, biography, links, songs);
    }

    private Song buildSongFromMap( Map<String, Object> songData){
        String title, image, album, duration, id;
        int likes;
        List<String> genres = new ArrayList<>();
        List<Link> links = new ArrayList<>();
        List<ArtistPreview> featurings = new ArrayList<>();
        ArtistPreview artist = null;

        title = (songData.get("title") instanceof String ? (String)songData.get("title") : null);
        image = (songData.get("image") instanceof String ? (String)songData.get("image") : null);
        album = (songData.get("album") instanceof String ? (String)songData.get("album") : null);
        duration = (songData.get("duration") instanceof String ? (String)songData.get("duration") : null);
        id = (songData.get("id") instanceof String ? (String)songData.get("id") : null);
        likes = (songData.get("likes") instanceof Integer ? (Integer) songData.get("likes") : 0);
        double avgRating = (songData.get("avgRating") instanceof Double ? (Double) songData.get("avgRating") : -1);

        if(songData.get("mainArtist") != null){
            Map<String, Object> artistMap = (Map<String, Object>)songData.get("mainArtist");
            String artistUsername = (artistMap.get("username") instanceof String ? (String)artistMap.get("username") : null);
            String stageName = (artistMap.get("stageName") instanceof String ? (String)artistMap.get("stageName") : null);
            String artistImage = (artistMap.get("image") instanceof String ? (String)artistMap.get("image") : null);
            artist = new ArtistPreview(artistUsername, stageName, artistImage);
        }

        if(songData.get("featurings") != null) {
            Iterable<Map<String, Object>> featuringsList = (Iterable<Map<String, Object>>) songData.get("featurings");
            for (Map<String, Object> featArtistMap : featuringsList) {
                String featUsername = (featArtistMap.get("username") instanceof String ? (String) featArtistMap.get("username") : null);
                String featImage = (featArtistMap.get("image") instanceof String ? (String) featArtistMap.get("image") : null);
                String featStageName = (featArtistMap.get("stageName") instanceof String ? (String) featArtistMap.get("stageName") : null);
                featurings.add(new ArtistPreview(featUsername, featStageName, featImage));
            }
        }

        if(songData.get("links") != null) {
            Iterable<Map<String, Object>> linksList = (Iterable<Map<String, Object>>) songData.get("links");
            for (Map<String, Object> linkMap : linksList) {
                String linkName = (linkMap.get("name") instanceof String ? (String) linkMap.get("name") : null);
                String linkUrl = (linkMap.get("url") instanceof String ? (String) linkMap.get("url") : null);
                links.add(new Link(linkName, linkUrl));
            }
        }

        List<Review> loadedReviews = new ArrayList<>();

        if(songData.get("reviews") != null) {
            List<Map<String, Object>> reviewsData = (List<Map<String, Object>>) songData.get("reviews");
            for (Map<String, Object> review :
                    reviewsData) {
                loadedReviews.add(buildReviewFromMap(review));
            }
        }

        return new Song(id, artist, duration, title, image, album, loadedReviews, links, genres, featurings, likes, avgRating);
    }

    private Review buildReviewFromMap( Map<String, Object> reviewData){
        Object rating = reviewData.get("rating"),
                text = reviewData.get("text"),
                id = reviewData.get("id"),
                user = reviewData.get("user"),
                songID = reviewData.get("songID");

        return new Review(id, user, rating, text, songID);
    }


    public void close(){
        if(mongo != null){
            mongo.close();
        }
        if(neo4j != null){
            neo4j.close();
        }
    }
}
