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
     * Searches for a username inside the user collecion.
     *
     * @param username String containing the username whose existance we want to check.
     * @return True if the username exists, false otherwise.
     */
    boolean checkIfUsernameExists(String username);

    /**
     *
     * Searches for an email address inside the user collecion.
     *
     * @param email String containing the email address whose existance we want to check.
     * @return True if the email address exists, false otherwise.
     */
    boolean checkIfEmailExists(String email);

    /**
     *
     * Searches for a stage name inside the user collecion.
     *
     * @param stageName String containing the stage name whose existance we want to check.
     * @return True if the stage name exists, false otherwise.
     */
    boolean checkIfStageNameExists(String stageName);

    /**
     *
     * Searches for a request by username inside the reports collecion.
     *
     * @param username String containing the username of the user that performed the request.
     * @return True if the request exists, false otherwise.
     */
    boolean checkIfRequestExists(String username);

    /**
     *
     * Deletes all the reviews created by the user whose username is passed by parameter.
     *
     * @param username String containing the username of the user whose reviews are to be deleted.
     * @return True if the deletion has succeded, false otherwise.
     */
    boolean deleteReviews(String username);


    /**
     *
     * Checks if the credentials inserted during the login phase are correct.
     *
     * @param username String containing the username of the account we want to log in.
     * @param password String containing the password of the account we want to log in.
     * @return 0 if the credentials are correct, -1 if they are incorrect, 1 if the account we want to log in
     *         is connected to an Artist account.
     */
    int checkCredentials(String username, String password);

    /**
     *
     * Checks if the credentials inserted during the admin login phase are correct.
     *
     * @param username String containing the username of the admin account we want to log in.
     * @param password String containing the username of the admin account we want to log in.
     * @return false if the credentials do not match any user or if they match a user that is not an admin, true otherwise.
     */
    boolean checkAdminCredentials(String username, String password);

    /**
     *
     * Retrieves the user's information of a user by username.
     *
     * @param username String containing the username of the user whose information we want to retrieve.
     * @return An object corresponding to the user if it exists, null otherwise.
     */
    User getUser(String username);

    /**
     *
     * Retrieves the artist's information of an artist by username.
     *
     * @param username String containing the username of the artist whose information we want to retrieve.
     * @return An object corresponding to an artist if it exists, null otherwise.
     */
    Artist getArtist(String username);

    /**
     *
     * Retrieves the song's information of a user by username.
     *
     * @param songID String containing the ID of the song whose information we want to retrieve.
     * @return An object corresponding to a song if it exists, null otherwise.
     */
    Song getSong(String songID);

    /**
     *
     * Retrieves the previews of a list of artists.
     *
     * @param usernames List of Strings containing the usernames of the artists whose previews we want to retrieve.
     * @return A List of ArtistPreview objects to be shown if the usernames exists, null otherwise.
     */
    List<ArtistPreview> getArtistPreviews(List<String> usernames);

    /**
     *
     * Retrieves a number count of reviews of a song identified by its ID, starting by the startIndex-th review in terms
     * of posting date.
     *
     * @param songID String containing the ID of the song whose reviews we want to retrieve.
     * @param startIndex Integer containing the index of the review from where to start retrieving reviews.
     * @param count Number of reviews we want to retrieve.
     * @return A list of Review objects to be shown if the songID exists, null otherwise.
     */
    List<Review> getReviews(String songID, int startIndex, int count);

    /**
     *
     * Retrieves a number count of users followed by a user identified by username, starting by the startIndex-th user.
     *
     * @param username String containing the username of the user whose followed users we want to retrieve.
     * @param startIndex Integer containing the index of the followed user from where to start retrieving users.
     * @param count Number of users we want to retrieve.
     * @return A list of User objects to be shown if the username exists, null otherwise.
     */
    List<UserPreview> getFollowedUsers(String username, int startIndex, int count);

    /**
     *
     * Retrieves a number count of users that follow a user identified by username, starting by the startIndex-th user.
     *
     * @param username String containing the username of the user whose follower users we want to retrieve.
     * @param startIndex Integer containing the index of the follower user from where to start retrieving users.
     * @param count Number of users we want to retrieve.
     * @return A list of User objects to be shown if the username exists, null otherwise.
     */
    List<UserPreview> getFollowers(String username, int startIndex, int count);

    /**
     *
     * Retrieves a number count of artists followed by a user identified by username, starting by the startIndex-th artist.
     *
     * @param username String containing the username of the user whose followed artists we want to retrieve.
     * @param startIndex Integer containing the index of the followed artist from where to start retrieving artists.
     * @param count Number of artists we want to retrieve.
     * @return A list of Artists objects to be shown if the username exists, null otherwise.
     */
    List<ArtistPreview> getFollowedArtists(String username, int startIndex, int count);

    /**
     *
     * Retrieves a number count of artists that follow a user identified by username, starting by the startIndex-th artist.
     *
     * @param username String containing the username of the user whose follower artists we want to retrieve.
     * @param startIndex Integer containing the index of the follower artist from where to start retrieving artists.
     * @param count Number of artists we want to retrieve.
     * @return A list of Artists objects to be shown if the username exists, null otherwise.
     */
    List<ArtistPreview> getFollowingArtists(String username, int startIndex, int count);

    /**
     *
     * Retrieves a number count of songs liked by a user identified by username, starting by the startIndex-th song.
     *
     * @param username String containing the username of the user whose liked songs we want to retrieve.
     * @param startIndex Integer containing the index of the liked song from where to start retrieving songs.
     * @param count Number of songs we want to retrieve.
     * @return A list of Songs objects to be shown if the username exists, null otherwise.
     */
    List<SongPreview> getLikedSongs(String username, int startIndex, int count);

    /**
     *
     * Retrieves a number count of songs performed by an artist identified by username, starting by the startIndex-th song.
     *
     * @param username String containing the username of the artist whose songs we want to retrieve.
     * @param startIndex Integer containing the index of the artist's song from where to start retrieving songs.
     * @param count Number of songs we want to retrieve.
     * @return A list of Songs objects to be shown if the username exists and corresponds to an artist, null otherwise.
     */
    List<SongPreview> getArtistSongs(String username, int startIndex, int count);

    /**
     *
     * Adds a new user to the databases.
     *
     * @param newUser A User object containing the user's information chose by the user in the registration phase.
     * @return True if the registration process succeded, false otherwise.
     * @throws PersistenceInconsistencyException In case of error when saving the information.
     */
    boolean addNewUser(User newUser) throws PersistenceInconsistencyException;

    /**
     *
     * Deletes an existing user by username from the databases.
     *
     * @param username String containing the username of the user we want to delete.
     * @return True if the deletion process succeded, false otherwise.
     */
    boolean deleteUser(String username);

    /**
     *
     * Updates an existing user in the databases.
     *
     * @param user A User object containing the user's information chose by the user in the edit phase.
     * @return True if the update process succeded, false otherwise.
     */
    boolean updateUser(User user);

    /**
     *
     * Adds a new artist to the databases.
     *
     * @param newArtist An Artist object containing the artist's information to upgrade the correct user.
     * @param stageName String containing the stage name of the artist chose by the user in the upgrade request.
     * @return True if the upgrade process succeded, false otherwise.
     * @throws PersistenceInconsistencyException In case of error when saving the information.
     */
    boolean addArtist(Artist newArtist, String stageName) throws PersistenceInconsistencyException;

    /**
     *
     * Updates an existing artist in the databases.
     *
     * @param artist An Artist object containing the artist's information chose by the artist in the edit phase.
     * @return True if the update process succeded, false otherwise.
     */
    boolean updateArtist(Artist artist);

    /**
     *
     * Adds a new song to the databases.
     *
     * @param newSong A Song object containing the song's information chose by the artist in the creation phase.
     * @return True if the creation process succeded, false otherwise.
     * @throws PersistenceInconsistencyException In case of error when saving the information.
     */
    boolean addSong(Song newSong) throws PersistenceInconsistencyException;

    /**
     *
     * Deletes an existing song from the databases.
     *
     * @param song A Song object containing the information of the song we want to delete.
     * @return True if the deletion process succeded, false otherwise.
     */
    boolean deleteSong(Song song);

    /**
     *
     * Deletes an existing song from the databases.
     *
     * @param songID String containing the ID of the song we want to delete.
     * @return True if the deletion process succeded, false otherwise.
     */
    boolean deleteSong(String songID);

    /**
     *
     * Upgrades an existing song from the databases.
     *
     * @param song A Song object containing the information of the song we want to update.
     * @return True if the update process succeded, false otherwise.
     */
    boolean editSong(Song song);

    /**
     *
     * Adds a new song to the document database.
     *
     * @param review A Review object containing the information of the review we want to insert in the database.
     * @return True if the creation process succeded, false otherwise.
     */
    boolean addReview(Review review);

    /**
     *
     * Deletes an existing song from the document database.
     *
     * @param review A Review object containing the information of the review we want to delete.
     * @return True if the deletion process succeded, false otherwise.
     */
    boolean deleteReview(Review review);

    /**
     *
     * Deletes an existing song from the document database.
     *
     * @param reviewID String containing the ID of the review we want to delete.
     * @param song String containing the ID of the song containing the review we want to delete.
     * @return True if the deletion process succeded, false otherwise.
     */
    boolean deleteReview(String reviewID, String song);

    /**
     *
     * Checks if a user, identified by the username, is an artist.
     *
     * @param username String containing the username of the user to control.
     * @return True if the user is an artist, false otherwise.
     */
    boolean userIsArtist(String username);

    /**
     *
     * Retrieves a number limit of artists containing in their stage name the string "name" passed by argument,
     * starting by the startIndex-th artist if there are more than limit artists.
     *
     * @param name String that has to be contained in the stage name of the artists to be retrieved.
     * @param startIndex Integer containing the index of the artist from where to start retrieving artists.
     * @param limit Number of artists we want to retrieve.
     * @return A list of ArtistPreview objects to be shown if the name is contained in the stage name of at least one
     *         artist, null otherwise.
     */
    List<ArtistPreview> searchArtistByName(String name, int startIndex, int limit);

    /**
     *
     * Retrieves a number limit of songs containing in their title the string "name" passed by argument,
     * starting by the startIndex-th song if there are more than limit songs.
     *
     * @param name String that has to be contained in the title of the songs to be retrieved.
     * @param startIndex Integer containing the index of the song from where to start retrieving songs.
     * @param limit Number of songs we want to retrieve.
     * @return A list of SongPreview objects to be shown if the name is contained in the title of at least one
     *         song, null otherwise.
     */
    List<SongPreview> searchSongByTitle(String name, int startIndex, int limit);

    /**
     *
     * Retrieves a number limit of users containing in their username the string "name" passed by argument,
     * starting by the startIndex-th user if there are more than limit users.
     *
     * @param name String that has to be contained in the username of the users to be retrieved.
     * @param startIndex Integer containing the index of the user from where to start retrieving users.
     * @param limit Number of users we want to retrieve.
     * @return A list of UserPreviews objects to be shown if the name is contained in the username of at least one
     *         user, null otherwise.
     */
    List<UserPreview> searchUserByUsername(String name, int startIndex, int limit);

    /**
     *
     * Retrieves a number limit of artists containing in their username the string "name" passed by argument,
     * starting by the startIndex-th artist if there are more than limit artists.
     *
     * @param name String that has to be contained in the username of the artists to be retrieved.
     * @param startIndex Integer containing the index of the artist from where to start retrieving artists.
     * @param limit Number of artists we want to retrieve.
     * @return A list of ArtistPreviews objects to be shown if the name is contained in the username of at least one
     *         artist, null otherwise.
     */
    List<ArtistPreview> searchArtistsByUsername(String name, int startIndex, int limit);

    /**
     *
     * For each genre, displays the six songs that obtained the highest average rating,
     * only considering the user ratings posted in the last month.
     *
     * @return A Map object where the key are Strings containing the genre and the values are Lists of SongPreview objects
     *         corresponding to songs of that genre.
     */
    Map<String, List<SongPreview>> getBestSongsForEachGenre();

    /**
     *
     * Finds the most and least appreciated album for an artist, based on the ratings
     * given to the songs contained in each album.
     *
     * @param artist An Artist object containing the information of the artist whose best and worst album we want to retrieve.
     * @return A Map object containing two object, one with key "best" containing the best album and another with key
     *         "worst" containing the worst album.
     *
     */
    HashMap<String, String> getBestAndWorstAlbum(Artist artist);

    /**
     *
     * Finds the artists that have performed songs with the artists that are followed by the target user.
     *
     * @param user A User object containing the information of the target user.
     * @return A list of ArtistPreviews objects to be shown if the user exists, they follow at least one artist that have
     *         worked with at least another artist, null otherwise.
     */
    List<ArtistPreview> getCoworkersOfFollowedArtists(User user);

    /**
     *
     * For each genre, finds the most representative artist, combining the number of
     * the artist's songs belonging to that genre and their reviews.
     *
     * @return A Map object where the key are Strings containing the genre and the values are ArtistPreview objects
     *         corresponding to the most representative artist of that genre.
     */
    Map<String, ArtistPreview> getRepresentativeArtist();

    /**
     *
     * Gets songs liked by user's followed users.
     *
     * @param user A User object containing the information of the target user.
     * @return A list of SongPreview objects to be shown if the user exists, they follow at least one user that likes
     *         at least one song, null otherwise.
     */
    List<SongPreview> getFollowedUsersLikedSongs(User user);

    /**
     *
     * Gets artists that worked with user's followed artists.
     *
     * @param user A User object containing the information of the target user.
     * @return A list of ArtistPreview objects to be shown if the user exists, they follow at least one user that follows
     *         at least one artist, null otherwise.
     */
    List<ArtistPreview> getArtistsFollowedByFriends(User user);

    /**
     *
     * Gets users followed by user's followed users.
     *
     * @param user A User object containing the information of the target user.
     * @return A list of UserPreview objects to be shown if the user exists, they follow at least one user that follows
     *         at least another user, null otherwise.
     */
    List<UserPreview> getUsersFollowedByFriends(User user);

    /**
     *
     * Gets user's like-minded users, that like many songs that user likes, and a
     * list of songs liked by those users to be suggested to user.
     *
     * @param user A User object containing the information of the target user.
     * @return A Pair of Lists, where the first List contains the UserPreviews of the like-minded users, while the second
     *         List contains the SongPreviews of the songs liked by the corresponding like-minded users.
     */
    Pair<List<UserPreview>, List<SongPreview>> getLikeMindedUsersAndTheSongsTheyLike(User user);

    /**
     *
     * Gets top fans, i.e. followers of artist that also like many
     * of the songs performed by artist.
     *
     * @param artist An Artist object containing the information of the target artist.
     * @return A list of UserPreview objects to be shown if the artist exists, they are followed by at least one user that
     *         also likes many of the song the artist performs, null otherwise.
     *
     */
    List<UserPreview> getTopFans(Artist artist);


    /**
     *
     * Gets similar artists, which are largely followed by artist's followers.
     *
     * @param artist An Artist object containing the information of the target artist.
     * @return A list of ArtistPreview objects to be shown if the artist exists and there is at least
     *         one similar artist, null otherwise.
     *
     */
    List<ArtistPreview> getSimilarArtists(Artist artist);

    /**
     *
     * Gets popular songs performed by artist that are largely
     * liked by non-fans, i.e. users that do not follow artist.
     *
     * @param artist An Artist object containing the information of the target artist.
     * @return A list of SongPreview objects to be shown if the artist exists and there is at least
     *         one popular song, null otherwise.
     *
     */
    List<SongPreview> getPopularSongs(Artist artist);

    /**
     *
     * Adds a follow relationship between one user towards another.
     *
     * @param followed A User object containing the information of the followed user.
     * @param follower A User object containing the information of the user that has been followed.
     * @return True if the creation process succeded, false otherwise.
     */
    boolean addFollow(User followed, User follower);

    /**
     *
     * Deletes a follow relationship between one user towards another.
     *
     * @param followed A User object containing the information of the followed user.
     * @param follower A User object containing the information of the user that has been followed.
     * @return True if the deletion process succeded, false otherwise.
     */
    boolean deleteFollow(User followed, User follower);

    /**
     *
     * Checks if a follow relationship between one user towards another exists.
     *
     * @param followed A User object containing the information of the followed user.
     * @param follower A User object containing the information of the user that has been followed.
     * @return True if the follow relationship exists, false otherwise.
     */
    boolean checkFollow(User followed, User follower);

    /**
     *
     * Adds a like relationship between one user towards a song.
     *
     * @param user A User object containing the information of the user who added a like to the song.
     * @param song A Song object containing the information of the song that has received the like.
     * @return True if the creation process succeded, false otherwise.
     */
    boolean addLike(User user, Song song);

    /**
     *
     * Checks if a like relationship between one user towards a song exists.
     *
     * @param user A User object containing the information of the user who added a like to the song.
     * @param song A Song object containing the information of the song that has received the like.
     * @return True if the like relationship exists, false otherwise.
     */
    boolean checkLike(User user, Song song);

    /**
     *
     * Deletes a like relationship between one user towards a song.
     *
     * @param user A User object containing the information of the user who added a like to the song.
     * @param song A Song object containing the information of the song that has received the like.
     * @return True if the deletion process succeded, false otherwise.
     */
    boolean deleteLike(User user, Song song);

    /**
     *
     * Checks if a user already reviewed a song.
     *
     * @param user A User object containing the information of the user to be checked.
     * @param song A Song object containing the information of the song to be checked.
     * @return True if a review of the song made by the user already exists, false otherwise.
     */
    boolean checkIfUserReviewedSong(User user, Song song);

    /**
     *
     * Retrieve a list of the most reported users to be shown to the
     * admin.
     *
     * @return A List of Report object containing the information about the reported users and their reported reviews if
     *         they exist, null otherwise.
     */
    List<Report> getReports();

    /**
     *
     * Adds a report to the target review.
     *
     * @param review A Review object containing the information of the review the user wants to report.
     * @return True if the creation process succeded, false otherwise.
     */
    boolean reportReview(Review review);

    /**
     *
     * Adds a report to the target user.
     *
     * @param user A User object containing the information of the user that has been reported.
     * @return True if the creation process succeded, false otherwise.
     */
    boolean reportUser(User user);

    /**
     *
     * Retrieve a list of requests made by users to upgrade their account
     * to artist.
     *
     * @return A List of Request object containing the information about the request made by the users if
     *         they exist, null otherwise.
     */
    List<Request> getRequests();

    /**
     *
     * Adds a request from a user.
     *
     * @param request A User object containing the information of the request that has been made.
     * @return True if the creation process succeded, false otherwise.
     */
    boolean addRequest(Request request);

    /**
     *
     * Deletes a request from a user to get upgraded to artist.
     *
     * @param username String containing the username of the user whose request we want to delete.
     * @return True if the deletion process succeded, false otherwise.
     */
    boolean deleteRequest(String username);

    /**
     *
     * Deletes reports of a user.
     *
     * @param username String containing the username of the user whose report we want to delete.
     * @return True if the deletion process succeded, false otherwise.
     */
    boolean deleteReport(String username);


    /**
     *
     * Delete a comment by id.
     *
     * @param commentId String containing the id of the reported comment we want to delete from the report collection.
     * @return True if the deletion process succeded, false otherwise.
     */
    boolean deleteComment(String commentId);

    /**
     *
     * Retrieve all the reviews made by a user.
     *
     * @param username String containing the username of the user whose reviews we want to retrieve.
     * @return A List of Review objects containing the information about the review made by the target user if any,
     *         null otherwise.
     */
    List<Review> getReviewsByUsername(String username);

    /**
     *
     */
    void close();


}
