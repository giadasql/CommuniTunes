package it.unipi.iit.inginf.lsmdb.communitunes.persistence;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.Artist;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.Review;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.Song;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.User;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.ArtistPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.SongPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.UserPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.utilities.exceptions.PersistenceInconsistencyException;
import org.javatuples.Pair;
import org.javatuples.Triplet;

import java.util.HashMap;
import java.util.List;

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
     * @param username
     * @return
     */
    boolean deleteReviews(String username);

    /**
     * @param username
     * @param password
     * @return
     */
    boolean checkPassword(String username, String password);

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

    /**
     *
     * @param songID
     * @param nMax
     * @return
     */
    List<Review> getReviews(String songID, int nMax);

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

    /**
     *
     * @param review
     * @return
     */
    boolean editReview(Review review);

    /**
     *
     * For each genre, display the ten songs that obtained the highest average rating,
     * only considering the user ratings posted in the last month.
     *
     * @return
     */
    HashMap<String, List<SongPreview>> getSuggestedSongs();

    /**
     *
     * Find the most and least appreciated album for an artist, based on the ratings
     * given to the songs contained in each album.
     *
     * @param artist
     * @return a map with the two albums, with key "best" and "worst"
     *
     */
    HashMap<String, String> getApprAlbum(Artist artist);

    /**
     *
     * For each genre, find the most representative artist, combining the number of
     * the artist's songs belonging to that genre and their reviews.
     *
     * @param genre
     * @return
     *
     */
    List<Pair<String, ArtistPreview>> getRepresentativeArtist();

    /**
     *
     * Get songs liked by user's followed users.
     *
     * @param user
     * @return
     */
    List<SongPreview> getSuggestedSongs(User user);

    /**
     *
     * Get artists that worked with user's followed artists.
     *
     * @param user
     * @return
     */
    List<ArtistPreview> getSuggestedArtists(User user);

    /**
     *
     * Get users followed by user's followed users.
     *
     * @param user
     * @return
     */
    List<UserPreview> getSuggestedUsers(User user);

    /**
     *
     * Get user's like-minded users, that like many songs that user likes.
     *
     * @param user
     * @return
     */
    List<UserPreview> getLikeMindedUsers(User user);

    /**
     *
     * Get songs liked by user's like-minded users.
     *
     * @param user
     * @return
     *
     */
    List<SongPreview> getLikeMindedSongs(User user);

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


    /**
     *
     */
    void close();
}
