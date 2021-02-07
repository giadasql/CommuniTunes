package it.unipi.iit.inginf.lsmdb.communitunes.persistence;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.Artist;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.Review;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.Song;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.User;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.ArtistPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.SongPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.UserPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.utilities.configurations.ConfigReader;
import it.unipi.iit.inginf.lsmdb.communitunes.utilities.configurations.ConfigReaderFactory;
import it.unipi.iit.inginf.lsmdb.communitunes.utilities.configurations.ConfigReaderType;
import it.unipi.iit.inginf.lsmdb.communitunes.utilities.exceptions.PersistenceInconsistencyException;
import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.lang.reflect.Type;
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
        return mongo.updateUser(newUser);
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

        return mongo.updateArtist(newArtist) && neo4j.updateArtist(newArtist.Username, newArtist.StageName);
    }

        // TODO: Forse conviene passare l'artista insieme alla canzone?
    @Override
    public boolean addSong(Song newSong) throws PersistenceInconsistencyException {
        String mongoID = mongo.addSong(newSong.Artist, newSong.Duration, newSong.Title, newSong.Album);
        if(mongoID != null){
            int neoID = neo4j.addSong(newSong.Artist, newSong.Title, newSong.ID);
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
        boolean neo4jDelete = neo4j.deleteSong(song.Artist, song.Title);
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

        return mongo.updateSong(newSong) && neo4j.updateSong(newSong.Title, newSong.Artist, newSong.Featurings);
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
    public List<Pair<String, Integer>> getApprAlbum(Artist artist){ return null; }

    @Override
    public List<ArtistPreview> getRepresentativeArtist(String genre){ return null; }

    @Override
    public HashMap<String, List<SongPreview>> getSuggestedSongs() {
        return mongo.getSuggestedSongs();
    }

    @Override
    public List<ArtistPreview> getSuggestedArtists(User user) {
        return null;
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
    public boolean deleteReviews(String username) {
        return mongo.deleteReviews(username);
    }

    @Override
    public boolean checkPassword(String username, String password) {
        return mongo.checkPassword(username, password);
    }

    @Override
    public User getUser(String username) {
        Map<String, Object> userData = new HashMap<>(mongo.getUserData(username));
        userData.putAll(neo4j.getUserData(username));
        userData.put("username", username);
        return buildUserFromMap(userData);
    }

    @Override
    public Artist getArtist(String username) {
        Map<String, Object> artistData = new HashMap<>(mongo.getArtistData(username));
        artistData.putAll(neo4j.getArtistData(username));
        artistData.put("username", username);
        return buildArtistFromMap(artistData);
    }

    @Override
    public Song getSong(String songID) {
        Map<String, Object> songData = new HashMap<>(mongo.getSongData(songID));
        songData.putAll(neo4j.getSongData(songID));
        songData.put("id", songID);
        return buildSongFromMap(songData);
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
        Object email = userData.get("email"),
                password = userData.get("password"),
                country = userData.get("country"),
                birthday = userData.get("birthday"),
                followed = userData.get("followed"),
                followers = userData.get("followers"),
                artistFollowers = userData.get("followerArtists"),
                artistFollowed = userData.get("followedArtists"),
                likes = userData.get("likes"),
                username = userData.get("username"),
                id = userData.get("id");
        return new User(email, username, password, country, birthday, likes, followed, artistFollowed, followers, artistFollowers, id);
    }

    private Artist buildArtistFromMap( Map<String, Object> artistData){
        Object email = artistData.get("email"),
                password = artistData.get("password"),
                country = artistData.get("country"),
                stageName = artistData.get("stageName"),
                biography = artistData.get("biography"),
                birthday = artistData.get("birthday"),
                followed = artistData.get("followed"),
                followers = artistData.get("followers"),
                artistFollowers = artistData.get("followerArtists"),
                artistFollowed = artistData.get("followedArtists"),
                likes = artistData.get("likes"),
                songs = artistData.get("songs"),
                image = artistData.get("image"),
                yearsActive = artistData.get("activity"),
                username = artistData.get("username"),
                id = artistData.get("id");

        return new Artist(email, username, password, country, birthday, likes, followed, artistFollowed, followers, artistFollowers, stageName, biography, image, yearsActive, songs, id);
    }

    private Song buildSongFromMap( Map<String, Object> songData){
        Object artist = songData.get("mainArtist"),
                title = songData.get("title"),
                image = songData.get("image"),
                album = songData.get("album"),
                duration = songData.get("duration"),
                link = songData.get("link"),
                genres = songData.get("genres"),
                featurings = songData.get("featurings"),
                loadedLikes = songData.get("likers"),
                songID = songData.get("id");

        // TODO: find a way to not do this high level operation at Persistence level
        List<Review> loadedReviews = new ArrayList<>();
        try{
            List<Map<String, Object>> reviewsData = (List<Map<String, Object>>)songData.get("reviews");
            for(Map<String, Object> review:
                    reviewsData){
                loadedReviews.add(buildReviewFromMap(review));
            }
        }
        catch(ClassCastException exc){
            // TODO: log
        }

        return new Song(artist, duration, title, image, album, loadedReviews, link, loadedLikes, genres, featurings, songID);
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
