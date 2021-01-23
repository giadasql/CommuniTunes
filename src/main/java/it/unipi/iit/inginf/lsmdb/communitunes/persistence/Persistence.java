package it.unipi.iit.inginf.lsmdb.communitunes.persistence;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.Artist;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.Review;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.Song;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.User;
import it.unipi.iit.inginf.lsmdb.communitunes.utilities.exceptions.PersistenceInconsistencyException;
import org.javatuples.Pair;
import org.javatuples.Triplet;

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
     * @param newArtist
     * @return
     * @throws PersistenceInconsistencyException
     */
    boolean addArtist(Artist newArtist) throws PersistenceInconsistencyException;

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
     * @param user
     * @return
     */
    List<Pair<String, String>> getSuggestedSongs(User user);

    /**
     *
     * @param genre
     * @return
     */
    List<Pair<String, String>> getSuggestedSongs(String genre);

    /**
     *
     * @param user
     * @return
     */
    List<String> getSuggestedArtists(User user);

    /**
     *
     * @param user
     * @return
     */
    List<String> getSuggestedUsers(User user);

    /**
     *
     */
    void close();
}
