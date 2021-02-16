package it.unipi.iit.inginf.lsmdb.communitunes.persistence;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.*;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.ArtistPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.SongPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.UserPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.utilities.configurations.ConfigReader;
import it.unipi.iit.inginf.lsmdb.communitunes.utilities.exceptions.PersistenceInconsistencyException;
import org.javatuples.Pair;
import org.javatuples.Triplet;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

class PersistenceImplementation implements Persistence {

    private final ConfigReader reader;

    private MongoDriver mongo;
    private Neo4jDriver neo4j;


    public PersistenceImplementation(ConfigReader settingsReader) {
        this.reader = settingsReader;
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
        if (username == null) {
            throw new IllegalArgumentException("username cannot be null");
        }
        return mongo.checkIfUsernameExists(username) || neo4j.checkIfUsernameExists(username);
    }

    public boolean checkIfEmailExists(String email){
        if (email == null) {
            throw new IllegalArgumentException("email cannot be null");
        }
        return mongo.checkIfEmailExists(email);
    }

    public boolean checkIfStageNameExists(String stageName)
    {
        if (stageName == null) {
            throw new IllegalArgumentException("stageName cannot be null");
        }
        return mongo.checkIfStageNameExists(stageName);
    }

    public boolean checkIfRequestExists(String username) {
        if (username == null) {
            throw new IllegalArgumentException("username cannot be null");
        }
        return mongo.checkIfRequestExists(username);
    }

    public boolean addNewUser(User newUser) throws PersistenceInconsistencyException {
        if (newUser == null) {
            throw new IllegalArgumentException("newUser cannot be null");
        }
        String mongoID = mongo.addUser(newUser.username, newUser.string, newUser.password);
        if(mongoID != null){
            int neoID = neo4j.addUser(newUser.username);
            if(neoID != -1){
                return true;
            }
            else{
                if(mongo.deleteUser(newUser.username)){
                    return false;
                }
                else{
                    throw new PersistenceInconsistencyException("User " + newUser.username + " was not correctly added, but due to unexpected errors the user might be present in the database. This my cause inconsistencies.");
                }
            }
        }
        else{
            return false;
        }
    }

    @Override
    public boolean deleteUser(String username) {
        if (username == null) {
            throw new IllegalArgumentException("username cannot be null");
        }
        boolean neo4jDelete = neo4j.deleteUser(username);
        boolean mongoDelete = mongo.deleteUser(username);
        return (mongoDelete && neo4jDelete && deleteReviews(username));
    }

    @Override
    public boolean updateUser(User newUser) {
        if (newUser == null) {
            throw new IllegalArgumentException("newUser cannot be null");
        }
        return mongo.updateUser(newUser.id, newUser.password, newUser.string, newUser.country, newUser.birthday, newUser.firstName, newUser.lastName, newUser.image) && neo4j.updateUser(newUser.username, newUser.image);
    }

    @Override
    public boolean addArtist(Artist newArtist, String stageName) {
        if (newArtist == null) {
            throw new IllegalArgumentException("newArtist cannot be null");
        }
        if (stageName == null) {
            throw new IllegalArgumentException("stageName cannot be null");
        }
        newArtist.stageName = stageName;
        return mongo.addArtist(newArtist.username, stageName);
    }

    @Override
    public boolean updateArtist(Artist newArtist) {
        if (newArtist == null) {
            throw new IllegalArgumentException("newArtist cannot be null");
        }
        return mongo.updateArtist(newArtist.id, newArtist.password, newArtist.string, newArtist.country, newArtist.birthday, newArtist.firstName, newArtist.lastName, newArtist.image, newArtist.activeYears, newArtist.biography, newArtist.stageName, newArtist.links) && neo4j.updateArtist(newArtist.username, newArtist.stageName, newArtist.image);
    }


    @Override
    public boolean addSong(Song newSong) throws PersistenceInconsistencyException {
        if (newSong == null) {
            throw new IllegalArgumentException("newSong cannot be null");
        }
        String mongoID = mongo.addSong(newSong.artist.username, newSong.duration, newSong.title, newSong.album, newSong.image, newSong.links, newSong.genres);
        newSong.id = mongoID;
        if(mongoID != null){
            boolean success = neo4j.addSong(newSong.artist.username, newSong.title, newSong.id, newSong.featList.stream().map(x -> x.username).collect(Collectors.toList()), newSong.image);
            if(success){
                return true;
            }
            else{
                if(mongo.deleteSong(newSong.id)){
                    return false;
                }
                else{
                    throw new PersistenceInconsistencyException("Song " + newSong.title + " was not correctly added, but due to unexpected errors the song might be present in the database, causing an inconsistency.");
                }
            }
        }
        else{
            return false;
        }
    }

    @Override
    public boolean deleteSong(Song song) {
        if (song == null) {
            throw new IllegalArgumentException("song cannot be null");
        }
        return deleteSong(song.id);
    }

    @Override
    public boolean deleteSong(String songID) {
        if (songID == null) {
            throw new IllegalArgumentException("songID cannot be null");
        }
        boolean neo4jDelete = neo4j.deleteSong(songID);
        boolean mongoDelete = mongo.deleteSong(songID);
        return (mongoDelete && neo4jDelete);
    }

    @Override
    public boolean editSong(Song newSong) {
        if (newSong == null) {
            throw new IllegalArgumentException("newSong cannot be null");
        }
        return mongo.updateSong(newSong.id, newSong.image, newSong.album, newSong.genres, newSong.links) && neo4j.updateSong(newSong.id, newSong.title, newSong.artist.username, newSong.image);
    }

    @Override
    public boolean addReview(Review review) {
        if (review == null) {
            throw new IllegalArgumentException("review cannot be null");
        }
        String insertedId = mongo.addReview(review.user, review.rating, review.text, review.song);
        if(insertedId != null){
            review.id = insertedId;
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteReview(Review review) {
        if (review == null) {
            throw new IllegalArgumentException("review cannot be null");
        }
        return deleteReview(review.id, review.song);
    }

    @Override
    public boolean deleteReview(String reviewID, String song) {
        if (reviewID == null) {
            throw new IllegalArgumentException("reviewID cannot be null");
        }
        if (song == null) {
            throw new IllegalArgumentException("song cannot be null");
        }
        return mongo.deleteReview(song, reviewID);
    }

    @Override
    public boolean userIsArtist(String username) {
        if (username == null) {
            throw new IllegalArgumentException("username cannot be null");
        }
        return mongo.userIsArtist(username);
    }

    @Override
    public List<ArtistPreview> searchArtistByName(String name, int startIndex, int limit) {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
        List<HashMap<String, Object>> artistPreviewMaps = mongo.getArtistByName(name, startIndex, limit);
        List<ArtistPreview> artistPreviews = new ArrayList<>();
        for (HashMap<String, Object> artistPreviewMap:
             artistPreviewMaps) {
            artistPreviews.add(buildArtistPreviewFromMap(artistPreviewMap));
        }
        return artistPreviews;
    }

    @Override
    public List<SongPreview> searchSongByTitle(String title, int startIndex, int limit) {
        if (title == null) {
            throw new IllegalArgumentException("title cannot be null");
        }
        List<HashMap<String, Object>> songPreviewMaps = mongo.getSongByTitle(title, startIndex, limit);
        List<SongPreview> songPreviews = new ArrayList<>();
        for (HashMap<String, Object> songPreviewMap:
                songPreviewMaps) {
            songPreviews.add(buildSongPreviewFromMap(songPreviewMap));
        }
        return songPreviews;
    }

    @Override
    public List<UserPreview> searchUserByUsername(String name, int startIndex, int limit) {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }

        List<HashMap<String, Object>> userPreviewMaps = mongo.searchUserByUsername(name, startIndex, limit);
        List<UserPreview> userPreviews = new ArrayList<>();
        for (HashMap<String, Object> userPreviewMap:
                userPreviewMaps) {
            userPreviews.add(buildUserPreviewFromMap(userPreviewMap));
        }
        return userPreviews;
    }

    @Override
    public List<ArtistPreview> searchArtistsByUsername(String name, int startIndex, int limit) {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
        List<HashMap<String, Object>> artistPreviewMaps = mongo.searchArtistByUsername(name, startIndex, limit);
        List<ArtistPreview> artistPreviews = new ArrayList<>();
        for (HashMap<String, Object> artistPreviewMap:
                artistPreviewMaps) {
            artistPreviews.add(buildArtistPreviewFromMap(artistPreviewMap));
        }
        return artistPreviews;
    }

    @Override
    public List<UserPreview> getFollowedUsers(String username, int startIndex, int count){
        if (username == null) {
            throw new IllegalArgumentException("username cannot be null");
        }
        List<UserPreview> result = new  ArrayList<>();
        List<Map<String, Object>> users = neo4j.getFollowedUsers(username, startIndex, count);
        for (Map<String, Object> user:
             users) {
            result.add(buildUserPreviewFromMap(user));
        }
        return result;
    }

    @Override
    public List<UserPreview> getFollowers(String username, int startIndex, int count){
        if (username == null) {
            throw new IllegalArgumentException("username cannot be null");
        }

        List<UserPreview> result = new  ArrayList<>();
        List<Map<String, Object>> users = neo4j.getFollowers(username, startIndex, count);
        for (Map<String, Object> user:
                users) {
            result.add(buildUserPreviewFromMap(user));
        }
        return result;
    }

    @Override
    public List<ArtistPreview> getFollowedArtists(String username, int startIndex, int count){
        if (username == null) {
            throw new IllegalArgumentException("username cannot be null");
        }
        List<ArtistPreview> result = new  ArrayList<>();
        List<Map<String, Object>> artists = neo4j.getFollowedArtists(username, startIndex, count);
        for (Map<String, Object> artist:
                artists) {
            result.add(buildArtistPreviewFromMap(artist));
        }
        return result;
    }

    @Override
    public List<ArtistPreview> getFollowingArtists(String username, int startIndex, int count){
        if (username == null) {
            throw new IllegalArgumentException("username cannot be null");
        }
        List<ArtistPreview> result = new  ArrayList<>();
        List<Map<String, Object>> artists = neo4j.getFollowingArtists(username, startIndex, count);
        for (Map<String, Object> artist:
                artists) {
            result.add(buildArtistPreviewFromMap(artist));
        }
        return result;
    }

    @Override
    public List<SongPreview> getLikedSongs(String username, int startIndex, int count){
        if (username == null) {
            throw new IllegalArgumentException("username cannot be null");
        }
        List<SongPreview> result = new  ArrayList<>();
        List<Map<String, Object>> songs = neo4j.getLikedSongs(username, startIndex, count);
        for (Map<String, Object> song:
                songs) {
            result.add(buildSongPreviewFromMap(song));
        }
        return result;
    }

    @Override
    public List<SongPreview> getArtistSongs(String username, int startIndex, int count){
        if (username == null) {
            throw new IllegalArgumentException("username cannot be null");
        }
        List<SongPreview> result = new  ArrayList<>();
        List<Map<String, Object>> songs = neo4j.getArtistSongs(username, startIndex, count);
        for (Map<String, Object> song:
                songs) {
            result.add(buildSongPreviewFromMap(song));
        }
        return result;
    }

    @Override
    public List<SongPreview> getFollowedUsersLikedSongs(User user) {
        if (user == null) {
            throw new IllegalArgumentException("user cannot be null");
        }
        List<SongPreview> result = new  ArrayList<>();
        List<Map<String, Object>> songs = neo4j.getFollowedUsersLikedSongs(user.username);
        for (Map<String, Object> song:
                songs) {
            result.add(buildSongPreviewFromMap(song));
        }
        return result;
    }

    @Override
    public HashMap<String, String> getBestAndWorstAlbum(Artist artist){
        if (artist == null) {
            throw new IllegalArgumentException("artist cannot be null");
        }
        return mongo.getBestAndWorstAlbum(artist.username);
    }

    @Override
    public List<ArtistPreview> getCoworkersOfFollowedArtists(User user) {
        if (user == null) {
            throw new IllegalArgumentException("user cannot be null");
        }
        List<ArtistPreview> result = new  ArrayList<>();
        List<Map<String, Object>> artists = neo4j.getCoworkersOfFollowedArtists(user.username);
        for (Map<String, Object> artist:
                artists) {
            result.add(buildArtistPreviewFromMap(artist));
        }
        return result;
    }

    @Override
    public Map<String, ArtistPreview> getRepresentativeArtist() {
        Map<String, ArtistPreview> analyticResult = new HashMap<>();
        Map<String, String> mongoResult = mongo.getRepresentativeArtist();
        Map<String, ArtistPreview> usernameToPreview = new HashMap<>();
        Collection<String> artists = mongoResult.values();
        List<ArtistPreview> artistPreviews = getArtistPreviews(new ArrayList<>(artists));
        for(ArtistPreview preview :
            artistPreviews){
            usernameToPreview.put(preview.username, preview);
        }

        for(String genre :
                mongoResult.keySet()){
            analyticResult.put(genre, usernameToPreview.getOrDefault(mongoResult.getOrDefault(genre, ""), null));
        }

        return  analyticResult;
    }


    @Override
    public Map<String, List<SongPreview>> getBestSongsForEachGenre() {
        Map<String, List<SongPreview>> analyticResult = new HashMap<>();
        Map<String, List<Map<String, Object>>>  result = mongo.getBestSongsForEachGenre();
        for (String genre:
             result.keySet()) {
            List<Map<String, Object>> songsData = result.get(genre);
            analyticResult.put(genre, new ArrayList<>());
            for (Map<String, Object> songMap:
                 songsData) {
                analyticResult.get(genre).add(buildSongPreviewFromMap(songMap));
            }
        }
        return analyticResult;
    }

    @Override
    public List<ArtistPreview> getArtistsFollowedByFriends(User user) {
        if (user == null) {
            throw new IllegalArgumentException("user cannot be null");
        }
        List<ArtistPreview> result = new  ArrayList<>();
        List<Map<String, Object>> artists = neo4j.getArtistsFollowedByFriends(user.username);
        for (Map<String, Object> artist:
                artists) {
            result.add(buildArtistPreviewFromMap(artist));
        }
        return result;
    }

    @Override
    public List<UserPreview> getUsersFollowedByFriends(User user) {
        if (user == null) {
            throw new IllegalArgumentException("user cannot be null");
        }
        List<UserPreview> result = new  ArrayList<>();
        List<Map<String, Object>> users = neo4j.getUsersFollowedByFriends(user.username);
        for (Map<String, Object> suggestion:
                users) {
            result.add(buildUserPreviewFromMap(suggestion));
        }
        return result;
    }

    @Override
    public Pair<List<UserPreview>, List<SongPreview>> getLikeMindedUsersAndTheSongsTheyLike(User user){
        if (user == null) {
            throw new IllegalArgumentException("user cannot be null");
        }
        List<UserPreview> suggestedUsers = new  ArrayList<>();
        List<SongPreview> suggestedSongs = new  ArrayList<>();
        Map<String, List<Map<String, Object>>> suggestions = neo4j.getLikeMindedUsersAndTheSongsTheyLike(user.username);
        for (Map<String, Object> suggestion:
                suggestions.getOrDefault("users", new ArrayList<>())) {
            suggestedUsers.add(buildUserPreviewFromMap(suggestion));
        }
        for (Map<String, Object> suggestion:
                suggestions.getOrDefault("songs", new ArrayList<>())) {
            suggestedSongs.add(buildSongPreviewFromMap(suggestion));
        }
        return new Pair<>(suggestedUsers, suggestedSongs);
    }


    @Override
    public List<UserPreview> getTopFans(Artist artist){
        if (artist == null) {
            throw new IllegalArgumentException("artist cannot be null");
        }
        List<UserPreview> result = new  ArrayList<>();
        List<Map<String, Object>> users = neo4j.getTopFans(artist.username);
        for (Map<String, Object> suggestion:
                users) {
            result.add(buildUserPreviewFromMap(suggestion));
        }
        return result;
    }

    @Override
    public List<ArtistPreview> getSimilarArtists(Artist artist){
        if (artist == null) {
            throw new IllegalArgumentException("artist cannot be null");
        }
        List<ArtistPreview> result = new  ArrayList<>();
        List<Map<String, Object>> artists = neo4j.getSimilarArtists(artist.username);
        for (Map<String, Object> similarArtist:
                artists) {
            result.add(buildArtistPreviewFromMap(similarArtist));
        }
        return result;
    }

    @Override
    public List<SongPreview> getPopularSongs(Artist artist) {
        if (artist == null) {
            throw new IllegalArgumentException("artist cannot be null");
        }
        List<SongPreview> analyticResult = new ArrayList<>();
        List<Map<String, Object>> result = neo4j.getPopularSongs(artist.username);
        for (Map<String, Object> song:
                result) {
            analyticResult.add(buildSongPreviewFromMap(song));
        }
        return analyticResult;
    }

    @Override
    public boolean addFollow(User followed, User follower) {
        if (follower == null) {
            throw new IllegalArgumentException("follower cannot be null");
        }
        if (followed == null) {
            throw new IllegalArgumentException("followed cannot be null");
        }
        return neo4j.addFollow(followed.username, follower.username);
    }

    @Override
    public boolean checkFollow(User followed, User follower) {
        if (follower == null) {
            throw new IllegalArgumentException("follower cannot be null");
        }
        if (followed == null) {
            throw new IllegalArgumentException("followed cannot be null");
        }
        return neo4j.checkFollow(followed.username, follower.username);
    }

    @Override
    public boolean deleteFollow(User followed, User follower) {
        if (follower == null) {
            throw new IllegalArgumentException("follower cannot be null");
        }
        if (followed == null) {
            throw new IllegalArgumentException("followed cannot be null");
        }
        return neo4j.deleteFollow(followed.username, follower.username);
    }

    @Override
    public boolean addLike(User user, Song song) {
        if (user == null) {
            throw new IllegalArgumentException("user cannot be null");
        }
        if (song == null) {
            throw new IllegalArgumentException("song cannot be null");
        }
        return neo4j.addLike(user.username, song.id);
    }

    @Override
    public boolean checkLike(User user, Song song) {
        if (user == null) {
            throw new IllegalArgumentException("user cannot be null");
        }
        if (song == null) {
            throw new IllegalArgumentException("song cannot be null");
        }
        return neo4j.checkLike(user.username, song.id);
    }

    @Override
    public boolean deleteLike(User user, Song song) {
        if (user == null) {
            throw new IllegalArgumentException("user cannot be null");
        }
        if (song == null) {
            throw new IllegalArgumentException("song cannot be null");
        }
        return neo4j.deleteLike(user.username, song.id);
    }

    @Override
    public boolean checkIfUserReviewedSong(User user, Song song) {
        if (user == null) {
            throw new IllegalArgumentException("user cannot be null");
        }
        if (song == null) {
            throw new IllegalArgumentException("song cannot be null");
        }
        return mongo.checkIfUserReviewedSong(user.username, song.id);
    }

    @Override
    public boolean deleteReviews(String username) {
        if (username == null) {
            throw new IllegalArgumentException("username cannot be null");
        }
        return mongo.deleteReviews(username);
    }

    @Override
    public int checkCredentials(String username, String password) {
        if (username == null) {
            throw new IllegalArgumentException("username cannot be null");
        }
        if (password == null) {
            throw new IllegalArgumentException("password cannot be null");
        }

        return mongo.checkCredentials(username, password);
    }

    @Override
    public boolean checkAdminCredentials(String username, String password) {
        if (username == null) {
            throw new IllegalArgumentException("username cannot be null");
        }
        if (password == null) {
            throw new IllegalArgumentException("password cannot be null");
        }

        return mongo.checkAdminCredentials(username, password);
    }

    @Override
    public boolean addRequest(Request request) {
        if (request == null) {
            throw new IllegalArgumentException("request cannot be null");
        }
        return mongo.addRequest(request.username, request.requestedStageName);
    }

    @Override
    public boolean deleteRequest(String username){
        if (username == null) {
            throw new IllegalArgumentException("username cannot be null");
        }
        return mongo.deleteRequest(username);
    }

    @Override
    public boolean deleteReport(String username){
        if (username == null) {
            throw new IllegalArgumentException("username cannot be null");
        }
        return mongo.deleteReport(username);
    }

    @Override
    public boolean deleteComment(String commentId){
        if (commentId == null) {
            throw new IllegalArgumentException("commentId cannot be null");
        }
        return mongo.deleteComment(commentId);
    }

    @Override
    public List<Report> getReports(){
        List<Triplet<String, Integer, List<HashMap<String, String>>>> reports =  mongo.getReports();
        List<Report> res = new ArrayList<>();
        for(Triplet<String, Integer, List<HashMap<String, String>>> t : reports){
            res.add(new Report(t.getValue0(), t.getValue1(), t.getValue2()));
        }
        return res;
    }

    @Override
    public boolean reportReview(Review review) {
        if (review == null) {
            throw new IllegalArgumentException("review cannot be null");
        }
        return mongo.reportReview(review.user, review.id, review.text, review.song);
    }

    @Override
    public boolean reportUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("user cannot be null");
        }
        return mongo.reportUser(user.username);
    }

    public List<Request> getRequests(){
        List<HashMap<String, String>> requests =  mongo.getRequests();
        List<Request> res = new ArrayList<>();
        for(HashMap<String, String> requestMap : requests){
            res.add(new Request(requestMap.get("id"), requestMap.get("username"), requestMap.get("requestedStageName")));
        }
        return res;
    }

    @Override
    public User getUser(String username) {
        if (username == null) {
            throw new IllegalArgumentException("username cannot be null");
        }
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
        if (username == null) {
            throw new IllegalArgumentException("username cannot be null");
        }
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
        if (songID == null) {
            throw new IllegalArgumentException("songID cannot be null");
        }
        Map<String, Object> songData = mongo.getSongData(songID);
        if(songData != null){
            songData.putAll(neo4j.getSongData(songID));
            songData.put("id", songID);
            return buildSongFromMap(songData);
        }
        return null;
    }

    @Override
    public List<Review> getReviews(String songID, int startIndex, int count) {
        if (songID == null) {
            throw new IllegalArgumentException("songID cannot be null");
        }
        List<Review> toReturn = new ArrayList<>();
        List<Map<String, Object>> reviewsData = new ArrayList<>(mongo.getSongReviews(songID, startIndex, count));
        for (Map<String, Object> reviewData:
                reviewsData) {
            toReturn.add(buildReviewFromMap(reviewData));
        }

        return toReturn;
    }

    @Override
    public List<Review> getReviewsByUsername(String username){
        if (username == null) {
            throw new IllegalArgumentException("username cannot be null");
        }
        List<Review> res = new ArrayList<>();
        List<HashMap<String, Object>> reviewList = mongo.getReviewsByUsername(username);

        for(HashMap<String, Object> review : reviewList){
            res.add(buildReviewFromMap(review));
        }
        return res;
    }

    @Override
    public List<ArtistPreview> getArtistPreviews(List<String> usernames) {
        if (usernames == null) {
            throw new IllegalArgumentException("usernames cannot be null");
        }
        List<ArtistPreview> artistPreviews = new ArrayList<>();
        List<Map<String, Object>> artists = mongo.getArtistsWithFields("username", usernames, Arrays.asList("username", "image"));
        for (Map<String, Object> artist:
                artists) {
            artistPreviews.add(buildArtistPreviewFromMap(artist));
        }
        return artistPreviews;
    }

    private User buildUserFromMap(Map<String, Object> userData){
        String email, password, country, image, firstName, lastName, username, id;
        String birthday = null;
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
        birthday = (userData.get("birthday") instanceof String ? (String)userData.get("birthday") : null);
        id = (userData.get("id") instanceof String ? (String) userData.get("id") : null);
        
        Iterable<Map<String,Object>> followedArtistsList = (Iterable<Map<String,Object>>)userData.get("followedArtists");
        for (Map<String,Object> artistMap : followedArtistsList){
            String artistUsername = (artistMap.get("username") instanceof String ? (String)artistMap.get("username") : null);
            String artistImage = (artistMap.get("image") instanceof String ? (String)artistMap.get("image") : null);
            artistFollowed.add(new ArtistPreview(artistUsername, artistImage));
        }

        Iterable<Map<String,Object>> followerArtistsList = (Iterable<Map<String,Object>>)userData.get("followerArtists");
        for (Map<String,Object> artistMap : followerArtistsList){
            String artistUsername = (artistMap.get("username") instanceof String ? (String)artistMap.get("username") : null);
            String artistImage = (artistMap.get("image") instanceof String ? (String)artistMap.get("image") : null);
            artistFollowers.add(new ArtistPreview(artistUsername, artistImage));
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
        String birthday;
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
        birthday = (artistData.get("birthday") instanceof String ? (String)artistData.get("birthday") : null);
        id = (artistData.get("id") instanceof String ? (String) artistData.get("id") : null);
        stageName = (artistData.get("stageName") instanceof String ? (String) artistData.get("stageName") : null);
        biography = (artistData.get("biography") instanceof String ? (String) artistData.get("biography") : null);
        yearsActive = (artistData.get("activity") instanceof String ? (String) artistData.get("activity") : null);

        if(artistData.get("followedArtists") != null){
            Iterable<Map<String,Object>> followedArtistsList = (Iterable<Map<String,Object>>)artistData.getOrDefault("followedArtists", new ArrayList<>());
            for (Map<String,Object> artistMap : followedArtistsList){
                String artistUsername = (artistMap.get("username") instanceof String ? (String)artistMap.get("username") : null);
                String artistImage = (artistMap.get("image") instanceof String ? (String)artistMap.get("image") : null);
                artistFollowed.add(new ArtistPreview(artistUsername, artistImage));
            }
        }

        if(artistData.get("followerArtists") != null) {
            Iterable<Map<String, Object>> followerArtistsList = (Iterable<Map<String, Object>>) artistData.getOrDefault("followerArtists", new ArrayList<>());
            for (Map<String, Object> artistMap : followerArtistsList) {
                String artistUsername = (artistMap.get("username") instanceof String ? (String) artistMap.get("username") : null);
                String artistImage = (artistMap.get("image") instanceof String ? (String) artistMap.get("image") : null);
                artistFollowers.add(new ArtistPreview(artistUsername, artistImage));
            }
        }

        if(artistData.get("likes") != null) {
            Iterable<Map<String, Object>> likesList = (Iterable<Map<String, Object>>) artistData.getOrDefault("likes", new ArrayList<>());
            for (Map<String, Object> likeMap : likesList) {
                String songID = (likeMap.get("id") instanceof String ? (String) likeMap.get("id") : null);
                String title = (likeMap.get("title") instanceof String ? (String) likeMap.get("title") : null);
                String artistUsername = (likeMap.get("artist") instanceof String ? (String) likeMap.get("artist") : null);
                String songImage = (likeMap.get("image") instanceof String ? (String) likeMap.get("image") : null);
                likes.add(new SongPreview(songID, artistUsername, title, songImage));
            }
        }

        if(artistData.get("songs") != null) {
            Iterable<Map<String, Object>> songsList = (Iterable<Map<String, Object>>) artistData.getOrDefault("songs", new ArrayList<>());
            for (Map<String, Object> songMap : songsList) {
                String songID = (songMap.get("id") instanceof String ? (String) songMap.get("id") : null);
                String title = (songMap.get("title") instanceof String ? (String) songMap.get("title") : null);
                String artistUsername = (songMap.get("artist") instanceof String ? (String) songMap.get("artist") : null);
                String songImage = (songMap.get("image") instanceof String ? (String) songMap.get("image") : null);
                songs.add(new SongPreview(songID, artistUsername, title, songImage));
            }
        }

        if(artistData.get("followed") != null) {
            Iterable<Map<String, Object>> followedList = (Iterable<Map<String, Object>>) artistData.getOrDefault("followed", new ArrayList<>());
            for (Map<String, Object> userMap : followedList) {
                String userUsername = (userMap.get("username") instanceof String ? (String) userMap.get("username") : null);
                String userImage = (userMap.get("image") instanceof String ? (String) userMap.get("image") : null);
                followed.add(new UserPreview(userUsername, userImage));
            }
        }

        if(artistData.get("links") != null) {
            Iterable<Map<String, Object>> linksList = (Iterable<Map<String, Object>>) artistData.getOrDefault("links", new ArrayList<>());
            for (Map<String, Object> linkMap : linksList) {
                String linkName = (linkMap.get("name") instanceof String ? (String) linkMap.get("name") : null);
                String linkUrl = (linkMap.get("link") instanceof String ? (String) linkMap.get("link") : null);
                links.add(new Link(linkName, linkUrl));
            }
        }

        if(artistData.get("followers") != null) {
            Iterable<Map<String, Object>> followersList = (Iterable<Map<String, Object>>) artistData.getOrDefault("followers", new ArrayList<>());
            for (Map<String, Object> userMap : followersList) {
                String userUsername = (userMap.get("username") instanceof String ? (String) userMap.get("username") : null);
                String userImage = (userMap.get("image") instanceof String ? (String) userMap.get("image") : null);
                followers.add(new UserPreview(userUsername, userImage));
            }
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
            String artistImage = (artistMap.get("image") instanceof String ? (String)artistMap.get("image") : null);
            artist = new ArtistPreview(artistUsername, artistImage);
        }

        if(songData.get("featurings") != null) {
            Iterable<Map<String, Object>> featuringsList = (Iterable<Map<String, Object>>) songData.get("featurings");
            for (Map<String, Object> featArtistMap : featuringsList) {
                String featUsername = (featArtistMap.get("username") instanceof String ? (String) featArtistMap.get("username") : null);
                String featImage = (featArtistMap.get("image") instanceof String ? (String) featArtistMap.get("image") : null);
                featurings.add(new ArtistPreview(featUsername, featImage));
            }
        }

        if(songData.get("links") != null) {
            Iterable<Map<String, Object>> linksList = (Iterable<Map<String, Object>>) songData.get("links");
            for (Map<String, Object> linkMap : linksList) {
                String linkName = (linkMap.get("name") instanceof String ? (String) linkMap.get("name") : null);
                String linkUrl = (linkMap.get("link") instanceof String ? (String) linkMap.get("link") : null);
                links.add(new Link(linkName, linkUrl));
            }
        }

        if(songData.get("genres") != null) {
            Iterable<String> linksList = (List<String>)songData.get("genres");
            for (String genre : linksList) {
                genres.add(genre);
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

    private ArtistPreview buildArtistPreviewFromMap(Map<String, Object> artistData){
        String username = (artistData.get("username") instanceof String ? (String)artistData.get("username") : null);
        String image = (artistData.get("image") instanceof String ? (String)artistData.get("image") : null);
        return new ArtistPreview(username, image);
    }

    private UserPreview buildUserPreviewFromMap(Map<String, Object> userData){
        String username = (userData.get("username") instanceof String ? (String)userData.get("username") : null);
        String image = (userData.get("image") instanceof String ? (String)userData.get("image") : null);
        return new UserPreview(username, image);
    }

    private SongPreview buildSongPreviewFromMap(Map<String, Object> songData){
        String id = (songData.get("_id") instanceof String ? (String)songData.get("_id") : null);
        String image = (songData.get("image") instanceof String ? (String)songData.get("image") : null);
        String artistUsername = (songData.get("artist") instanceof String ? (String)songData.get("artist") : null);
        String title = (songData.get("title") instanceof String ? (String)songData.get("title") : null);
        return new SongPreview(id, artistUsername, title, image);
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
