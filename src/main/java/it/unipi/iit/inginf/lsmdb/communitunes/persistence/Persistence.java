package it.unipi.iit.inginf.lsmdb.communitunes.persistence;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.Artist;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.Review;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.Song;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.User;
import it.unipi.iit.inginf.lsmdb.communitunes.utilities.exceptions.PersistenceInconsistencyException;

public interface Persistence {
    boolean checkIfUsernameExists(String username);
    boolean checkIfEmailExists(String email);

    boolean deleteReviews(String username);
    boolean checkPassword(String username, String password);

    User getUser(String username);
    Artist getArtist(String username);
    Song getSong(String songID);
    Song getReview(String reviewID);

    boolean addNewUser(User newUser) throws PersistenceInconsistencyException;
    boolean deleteUser(User user);
    boolean updateUser(User user);

    boolean addArtist(Artist newArtist) throws PersistenceInconsistencyException;
    boolean deleteArtist(Artist artist);
    boolean updateArtist(Artist artist);

    boolean addSong(Song newSong) throws PersistenceInconsistencyException;
    boolean deleteSong(Song song);
    boolean editSong(Song song);

    boolean addReview(Review review);
    boolean deleteReview(Review review);
    boolean editReview(Review review);

    void close();
}
