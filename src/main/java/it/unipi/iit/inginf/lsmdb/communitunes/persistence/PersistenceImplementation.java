package it.unipi.iit.inginf.lsmdb.communitunes.persistence;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.Artist;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.Review;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.Song;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.User;
import it.unipi.iit.inginf.lsmdb.communitunes.utilities.configurations.ConfigReader;
import it.unipi.iit.inginf.lsmdb.communitunes.utilities.configurations.ConfigReaderFactory;
import it.unipi.iit.inginf.lsmdb.communitunes.utilities.configurations.ConfigReaderType;
import it.unipi.iit.inginf.lsmdb.communitunes.utilities.exceptions.PersistenceInconsistencyException;
import org.javatuples.Pair;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.util.*;

class PersistenceImplementation implements Persistence {

    private ConfigReader reader;

    private MongoDriver mongo;
    private Neo4jDriver neo4j;

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
    public boolean updateUser(User user) {
        return false;
    }

    @Override
    public boolean addArtist(Artist newArtist) throws PersistenceInconsistencyException {
        return false;
    }

    @Override
    public boolean deleteArtist(Artist artist) {
        return false;
    }

    @Override
    public boolean updateArtist(Artist artist) {
        return false;
    }

    @Override
    public boolean addSong(Song newSong) throws PersistenceInconsistencyException {
        return false;
    }

    @Override
    public boolean deleteSong(Song song) {
        return false;
    }

    @Override
    public boolean editSong(Song song) {
        return false;
    }

    @Override
    public boolean addReview(Review review) {
        return false;
    }

    @Override
    public boolean deleteReview(Review review) {
        return false;
    }

    @Override
    public boolean editReview(Review review) {
        return false;
    }

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
        Map<String, Object> userData = mongo.getUserData(username);
        userData.putAll(neo4j.getUserData(username));
        String email = null, password = null, country = null;
        Date birthday = null;
        List<String> followed = new ArrayList<>(), followers = new ArrayList<>(), artistFollowers = new ArrayList<>(), artistFollowed = new ArrayList<>();
        List<Pair<String, String>> likes = new ArrayList<>();
        for (String key :
                userData.keySet()) {
            try{
                switch (key){
                    case "email":
                        email = (String)userData.get(key);
                        break;
                    case "password":
                        password = (String)userData.get(key);
                        break;
                    case "country":
                        country = (String)userData.get(key);
                        break;
                    case "birthday":
                        birthday = (Date)userData.get(key);
                        break;
                    case "followed":
                        followed.addAll((List<String>)userData.get(key));
                        break;
                    case "followers":
                        followers.addAll((List<String>)userData.get(key));
                        break;
                    case "followedArtists":
                        artistFollowed.addAll((List<String>)userData.get(key));
                        break;
                    case "followerArtists":
                        artistFollowers.addAll((List<String>)userData.get(key));
                        break;
                    case "likes":
                        likes.addAll((List<Pair<String, String>>)userData.get(key));
                        break;
                    default:
                        break;
                }
            }
            catch (ClassCastException exception){
                // TODO: log the exception, but do nothing. Missing fields are okay.
            }
        }
        User toReturn = new User(username, email, password);
        toReturn.LoadedFollowers = followers;
        toReturn.LoadedFollowed = followed;
        toReturn.LoadedArtistFollowers = artistFollowers;
        toReturn.LoadedLikes = likes;
        toReturn.LoadedArtistFollowed = artistFollowed;
        toReturn.Country = country;
        toReturn.Birthday = birthday;
        toReturn.ID = (String)userData.get("id");
        return toReturn;
    }

    @Override
    public Artist getArtist(String username) {
        Map<String, Object> artistData = mongo.getArtistData(username);
        artistData.putAll(neo4j.getArtistData(username));
        String email = null, password = null, country = null, stageName = null;
        Date birthday = null;
        List<String> followed = new ArrayList<>(), followers = new ArrayList<>(), artistFollowers = new ArrayList<>(), artistFollowed = new ArrayList<>();
        List<Pair<String, String>> likes = new ArrayList<>(), songs = new ArrayList<>();
        for (String key :
                artistData.keySet()) {
            try{
                switch (key){
                    case "email":
                        email = (String)artistData.get(key);
                        break;
                    case "password":
                        password = (String)artistData.get(key);
                        break;
                    case "country":
                        country = (String)artistData.get(key);
                        break;
                    case "birthday":
                        birthday = (Date)artistData.get(key);
                        break;
                    case "stageName":
                        stageName = (String)artistData.get(key);
                        break;
                    case "followed":
                        followed.addAll((List<String>)artistData.get(key));
                        break;
                    case "followers":
                        followers.addAll((List<String>)artistData.get(key));
                        break;
                    case "followedArtists":
                        artistFollowed.addAll((List<String>)artistData.get(key));
                        break;
                    case "followerArtists":
                        artistFollowers.addAll((List<String>)artistData.get(key));
                        break;
                    case "likes":
                        likes.addAll((List<Pair<String, String>>)artistData.get(key));
                        break;
                    case "songs":
                        songs.addAll((List<Pair<String, String>>)artistData.get(key));
                        break;
                    default:
                        break;
                }
            }
            catch (ClassCastException exception){
                // TODO: log the exception, but do nothing. Missing fields are okay.
            }
        }
        Artist toReturn = new Artist(username, email, password);
        toReturn.LoadedFollowers = followers;
        toReturn.LoadedFollowed = followed;
        toReturn.LoadedArtistFollowers = artistFollowers;
        toReturn.LoadedLikes = likes;
        toReturn.LoadedArtistFollowed = artistFollowed;
        toReturn.Country = country;
        toReturn.Birthday = birthday;
        toReturn.StageName = stageName;
        toReturn.LoadedSongs = songs;
        toReturn.ID = (String)artistData.get("id");
        return toReturn;
    }

    @Override
    public Song getSong(String songID) {
        return null;
    }

    @Override
    public Song getReview(String reviewID) {
        return null;
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
