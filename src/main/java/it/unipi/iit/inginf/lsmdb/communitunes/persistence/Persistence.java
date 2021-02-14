package it.unipi.iit.inginf.lsmdb.communitunes.persistence;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.*;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.ArtistPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.SongPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.UserPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.utilities.exceptions.PersistenceInconsistencyException;
import org.javatuples.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Interface for the persistence management.
 * Connects the application with the specific database drivers.
 */
public interface Persistence {
    /**
     *
     * @param username
     * @return
     */
    boolean checkIfUsernameExists(String username);

    /**
     *
     * @param email
     * @return
     */
    boolean checkIfEmailExists(String email);

    /**
     *
     * @param stageName
     * @return
     */
    boolean checkIfStageNameExists(String stageName);

    /**
     *
     * @param username
     * @return
     */
    boolean checkIfRequestExists(String username);

    /**
     *
     * @param username
     * @return
     */
    boolean deleteReviews(String username);


    /**
     * @param username
     * @param password
     * @return
     */
    int checkCredentials(String username, String password);

    /**
     *
     * @param username
     * @param password
     * @return
     */
    boolean checkAdminCredentials(String username, String password);

    /**
     *
     * @param username
     * @return
     */
    User getUser(String username);

    /**
     *
     * @param username
     * @return
     */
    Artist getArtist(String username);

    /**
     *
     * @param songID
     * @return
     */
    Song getSong(String songID);


    List<ArtistPreview> getArtistPreviews(List<String> usernames);
    List<UserPreview> getUserPreviews(List<String> usernames);
    List<SongPreview> getSongPreviews(List<String> usernames);

    /**
     *
     * @param songID
     * @param nMax
     * @return
     */
    List<Review> getReviews(String songID, int nMax);


    List<Review> getReviews(String songID, int startIndex, int count);

    /**
     *
     * @param username
     * @return
     */
    List<UserPreview> getFollowedUsers(String username, int startIndex, int count);

    /**
     *
     * @param username
     * @return
     */
    List<UserPreview> getFollowers(String username, int startIndex, int count);

    /**
     *
     * @param username
     * @return
     */
    List<ArtistPreview> getFollowedArtists(String username, int startIndex, int count);

    /**
     *
     * @param username
     * @return
     */
    List<ArtistPreview> getFollowingArtists(String username, int startIndex, int count);

    /**
     *
     * @param username
     * @return
     */
    List<SongPreview> getLikedSongs(String username, int startIndex, int count);

    /**
     *
     * @param username
     * @return
     */
    List<SongPreview> getArtistSongs(String username, int startIndex, int count);

    /**
     *
     * @param newUser
     * @return
     * @throws PersistenceInconsistencyException
     */
    boolean addNewUser(User newUser) throws PersistenceInconsistencyException;

    /**
     *
     * @param user
     * @return
     */
    boolean deleteUser(User user);
    boolean deleteUser(String username);

    /**
     *
     * @param user
     * @return
     */
    boolean updateUser(User user);

    /**
     *
     * @param newArtist, stageName
     * @return
     * @throws PersistenceInconsistencyException
     */
    boolean addArtist(Artist newArtist, String stageName) throws PersistenceInconsistencyException;

    /**
     *
     * @param artist
     * @return
     */
    boolean deleteArtist(Artist artist);

    /**
     *
     * @param artist
     * @return
     */
    boolean updateArtist(Artist artist);

    /**
     *
     * @param newSong
     * @return
     * @throws PersistenceInconsistencyException
     */
    boolean addSong(Song newSong) throws PersistenceInconsistencyException;

    /**
     *
     * @param song
     * @return
     */
    boolean deleteSong(Song song);
    boolean deleteSong(String songID);

    /**
     *
     * @param song
     * @return
     */
    boolean editSong(Song song);

    /**
     *
     * @param review
     * @return
     */
    boolean addReview(Review review);

    /**
     *
     * @param review
     * @return
     */
    boolean deleteReview(Review review);
    boolean deleteReview(String reviewID, String song);

    /**
     *
     * @param review
     * @return
     */
    boolean editReview(Review review);

    boolean userIsArtist(String username);

    List<ArtistPreview> searchArtistByName(String name, int startIndex, int limit);
    List<SongPreview> searchSongByTitle(String name, int startIndex, int limit);
    List<UserPreview> searchUserByUsername(String name, int startIndex, int limit);
    List<ArtistPreview> searchArtistsByUsername(String name, int startIndex, int limit);

    /**
     *
     * For each genre, display the six songs that obtained the highest average rating,
     * only considering the user ratings posted in the last month.
     *
     * @return
     */
    Map<String, List<SongPreview>> getBestSongsForEachGenre();

    /**
     *
     * Find the most and least appreciated album for an artist, based on the ratings
     * given to the songs contained in each album.
     *
     * @param artist
     * @return a map with the two albums, with key "best" and "worst"
     *
     */
    HashMap<String, String> getBestAndWorstAlbum(Artist artist);


    List<ArtistPreview> getCoworkersOfFollowedArtists(User user);

    /**
     *
     * For each genre, find the most representative artist, combining the number of
     * the artist's songs belonging to that genre and their reviews.
     *
     * @return
     *
     */
    Map<String, ArtistPreview> getRepresentativeArtist();

    /**
     *
     * Get songs liked by user's followed users.
     *
     * @param user
     * @return
     */
    List<SongPreview> getFollowedUsersLikedSongs(User user);

    /**
     *
     * Get artists that worked with user's followed artists.
     *
     * @param user
     * @return
     */
    List<ArtistPreview> getArtistsFollowedByFriends(User user);

    /**
     *
     * Get users followed by user's followed users.
     *
     * @param user
     * @return
     */
    List<UserPreview> getUsersFollowedByFriends(User user);

    /**
     *
     * Get user's like-minded users, that like many songs that user likes, and a
     * list of songs liked by those users to be suggested to user.
     *
     * @param user
     * @return
     */
    Pair<List<UserPreview>, List<SongPreview>> getLikeMindedUsersAndTheSongsTheyLike(User user);

    /**
     *
     * Get top fans, i.e. followers of artist that also like many
     * of the songs performed by artist.
     *
     * @param artist
     * r@return
     *
     */
    List<UserPreview> getTopFans(Artist artist);


    /**
     *
     * Get similar artists, which are largely followed by artist's followers.
     *
     * @param artist
     * @return
     *
     */
    List<ArtistPreview> getSimilarArtists(Artist artist);

    /**
     *
     * Get popular songs performed by artist that are largely
     * liked by non-fans, i.e. users that do not follow artist.
     *
     * @param artist
     * @return
     *
     */
    List<SongPreview> getPopularSongs(Artist artist);

    boolean addFollow(User followed, User follower);
    boolean checkFollow(User followed, User follower);
    boolean deleteFollow(User followed, User follower);

    boolean addLike(User user, Song song);
    boolean checkLike(User user, Song song);
    boolean deleteLike(User user, Song song);

    boolean checkIfUserReviewedSong(User user, Song song);

    /**
     *
     * Retrieve a list of the most reported users to be shown to the
     * admin.
     *
     * @return
     */
    List<Report> getReports();

    boolean reportReview(Review review);

    boolean reportUser(User user);

    /**
     *
     * Retrieve a list of requests made by users to upgrade their account
     * to artist.
     *
     * @return
     */
    List<Request> getRequests();

    /**
     *
     * @param request
     * @return
     */
    boolean addRequest(Request request);

    /**
     *
     * Deletes a request from a user to get upgraded to artist.
     *
     * @param username
     */
    boolean deleteRequest(String username);

    /**
     *
     * Deletes reports of a user.
     *
     * @param username
     */
    boolean deleteReport(String username);


    /**
     *
     * Delete a comment by id.
     *
     * @param commentId
     * @return
     */
    boolean deleteComment(String commentId);

    /**
     *
     * Retrieve the preview of all the songs that contains the string title
     * in their title.
     *
     * @param title
     * @return
     */
    List<SongPreview> getSongs(String title);

    /**
     *
     * Retrieve all the reviews made by a user.
     *
     * @param username
     * @return
     */
    List<Review> getReviewsByUsername(String username);

    /**
     *
     */
    void close();


}
