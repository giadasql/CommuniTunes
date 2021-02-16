package it.unipi.iit.inginf.lsmdb.communitunes.persistence;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.Song;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.User;
import it.unipi.iit.inginf.lsmdb.communitunes.utilities.configurations.ConfigReader;
import it.unipi.iit.inginf.lsmdb.communitunes.utilities.configurations.ConfigReaderFactory;
import it.unipi.iit.inginf.lsmdb.communitunes.utilities.configurations.ConfigReaderType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class PersistenceImplementationTest {
    PersistenceImplementation persistenceImplementation;

    String config = "<?xml version=\"1.0\"?>\n" +
            "<Settings>\n" +
            "    <MongoDB connectionString=\"mongodb://172.16.3.88:27020,172.16.3.89:27020,172.16.3.90:27020/?retryWrites=true&amp;w=majority&amp;wtimeout=10000\"/>\n" +
            "    <Neo4j uri=\"bolt://172.16.3.90:7687\" username=\"neo4j\" password=\"root\"/>\n" +
            "</Settings>";

    @BeforeEach
    void setUp() throws IOException, ParserConfigurationException, SAXException {
        ConfigReader reader = ConfigReaderFactory.createConfigReaderFromString(ConfigReaderType.Xml, config);
        persistenceImplementation = new PersistenceImplementation(reader);
    }

    @Test
    void WHEN_checkIfUsernameExists_invokedWithNull_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.checkIfUsernameExists(null));
    }

    @Test
    void WHEN_checkIfEmailExists_invokedWithNull_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.checkIfEmailExists(null));
    }

    @Test
    void WHEN_checkIfStageNameExists_invokedWithNull_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.checkIfStageNameExists(null));
    }

    @Test
    void WHEN_checkIfRequestExists_invokedWithNull_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.checkIfRequestExists(null));
    }

    @Test
    void WHEN_addNewUser_invokedWithNull_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.addNewUser(null));
    }

    @Test
    void WHEN_deleteUser_invokedWithNull_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.deleteUser(null));
    }

    @Test
    void WHEN_updateUser_invokedWithNull_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.updateUser(null));
    }

    @Test
    void WHEN_addArtist_invokedWithNullArtist_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.addArtist(null, "name"));
    }

    @Test
    void WHEN_addArtist_invokedWithNullArtistAndStageName_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.addArtist(null, null));
    }

    @Test
    void WHEN_updateArtist_invokedWithNull_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.updateArtist(null));
    }

    @Test
    void WHEN_addSong_invokedWithNull_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.addSong(null));
    }

    @Test
    void WHEN_deleteSong_invokedWithNull_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.deleteSong((Song) null));
    }

    @Test
    void WHEN_deleteSong_invokedWithNullString_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.deleteSong((String) null));
    }

    @Test
    void WHEN_editSong_invokedWithNull_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.editSong(null));
    }

    @Test
    void WHEN_addReview_invokedWithNull_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.addReview(null));
    }


    @Test
    void WHEN_deleteReview_invokedWithNullReviewID_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.deleteReview(null, "song"));
    }

    @Test
    void WHEN_deleteReview_invokedWithNullSong_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.deleteReview("id", null));
    }

    @Test
    void WHEN_deleteReview_invokedWithNullReviewIDAndSong_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.deleteReview(null, null));
    }

    @Test
    void WHEN_searchArtistByName_invokedWithNull_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.searchArtistByName(null, 0, 0));
    }

    @Test
    void WHEN_searchSongByTitle_invokedWithNull_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.searchSongByTitle(null, 0, 0));
    }

    @Test
    void WHEN_searchUserByUsername_invokedWithNull_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.searchUserByUsername(null, 0, 0));
    }

    @Test
    void WHEN_searchArtistsByUsername_invokedWithNull_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.searchArtistsByUsername(null, 0, 0));
    }

    @Test
    void WHEN_getFollowedUsers_invokedWithNull_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.getFollowedUsers(null, 0, 0));
    }

    @Test
    void WHEN_userIsArtist_invokedWithNull_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.userIsArtist(null));
    }

    @Test
    void WHEN_getFollowers_invokedWithNull_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.getFollowers(null, 0, 0));
    }


    @Test
    void WHEN_getFollowedArtists_invokedWithNull_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.getFollowedArtists(null, 0, 0));
    }


    @Test
    void WHEN_getFollowingArtists_invokedWithNull_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.getFollowingArtists(null, 0, 0));
    }

    @Test
    void WHEN_getLikedSongs_invokedWithNull_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.getLikedSongs(null, 0, 0));
    }

    @Test
    void WHEN_getArtistSongs_invokedWithNull_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.getArtistSongs(null, 0, 0));
    }

    @Test
    void WHEN_getFollowedUsersLikedSongs_invokedWithNull_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.getFollowedUsersLikedSongs(null));
    }

    @Test
    void WHEN_getBestAndWorstAlbum_invokedWithNull_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.getBestAndWorstAlbum(null));
    }

    @Test
    void WHEN_getCoworkersOfFollowedArtists_invokedWithNull_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.getCoworkersOfFollowedArtists(null));
    }

    @Test
    void WHEN_getArtistsFollowedByFriends_invokedWithNull_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.getArtistsFollowedByFriends(null));
    }

    @Test
    void WHEN_getUsersFollowedByFriends_invokedWithNull_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.getUsersFollowedByFriends(null));
    }

    @Test
    void WHEN_getLikeMindedUsersAndTheSongsTheyLike_invokedWithNull_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.getLikeMindedUsersAndTheSongsTheyLike(null));
    }

    @Test
    void WHEN_getTopFans_invokedWithNull_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.getTopFans(null));
    }

    @Test
    void WHEN_getSimilarArtists_invokedWithNull_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.getSimilarArtists(null));
    }

    @Test
    void WHEN_getPopularSongs_invokedWithNull_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.getPopularSongs(null));
    }


    @Test
    void WHEN_addFollow_invokedWithNullFollowed_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.addFollow(null, new User("test", "test", "test")));
    }

    @Test
    void WHEN_addFollow_invokedWithNullFollower_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.addFollow(new User("test", "test", "test"), null));
    }

    @Test
    void WHEN_addFollow_invokedWithNullFollowedAndFollower_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.addFollow(null, null));
    }


    @Test
    void WHEN_checkFollow_invokedWithNullFollowed_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.checkFollow(null, new User("test", "test", "test")));
    }

    @Test
    void WHEN_checkFollow_invokedWithNullFollower_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.checkFollow(new User("test", "test", "test"), null));
    }

    @Test
    void WHEN_checkFollow_invokedWithNullFollowedAndFollower_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.checkFollow(null, null));
    }

    @Test
    void WHEN_deleteFollow_invokedWithNullFollowed_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.deleteFollow(null, new User("test", "test", "test")));
    }

    @Test
    void WHEN_deleteFollow_invokedWithNullFollower_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.deleteFollow(new User("test", "test", "test"), null));
    }

    @Test
    void WHEN_deleteFollow_invokedWithNullFollowedAndFollower_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.deleteFollow(null, null));
    }

    @Test
    void WHEN_addLike_invokedWithNullUser_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.addLike(null, new Song("title")));
    }

    @Test
    void WHEN_addLike_invokedWithNullSong_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.addLike(new User("test", "test", "test"), null));
    }

    @Test
    void WHEN_addLike_invokedWithNullSongAndUser_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.addLike(null, null));
    }

    @Test
    void WHEN_checkLike_invokedWithNullUser_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.checkLike(null, new Song("title")));
    }

    @Test
    void WHEN_checkLike_invokedWithNullSong_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.checkLike(new User("test", "test", "test"), null));
    }

    @Test
    void WHEN_checkLike_invokedWithNullSongAndUser_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.checkLike(null, null));
    }

    @Test
    void WHEN_deleteLike_invokedWithNullUser_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.deleteLike(null, new Song("title")));
    }

    @Test
    void WHEN_deleteLike_invokedWithNullSong_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.deleteLike(new User("test", "test", "test"), null));
    }

    @Test
    void WHEN_deleteLike_invokedWithNullSongAndUser_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.deleteLike(null, null));
    }


    @Test
    void WHEN_checkIfUserReviewedSong_invokedWithNullUser_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.checkIfUserReviewedSong(null, new Song("title")));
    }

    @Test
    void WHEN_checkIfUserReviewedSong_invokedWithNullSong_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.checkIfUserReviewedSong(new User("test", "test", "test"), null));
    }

    @Test
    void WHEN_checkIfUserReviewedSong_invokedWithNullSongAndUser_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.checkIfUserReviewedSong(null, null));
    }

    @Test
    void WHEN_deleteReviews_invokedWithNullUser_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.deleteReviews(null));
    }

    @Test
    void WHEN_checkCredentials_invokedWithNullUser_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.checkCredentials(null, "pass"));
    }


    @Test
    void WHEN_checkCredentials_invokedWithNullPassword_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.checkCredentials("username", null));
    }


    @Test
    void WHEN_checkAdminCredentials_invokedWithNullUserAndPassword_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.checkAdminCredentials(null, null));
    }


    @Test
    void WHEN_checkAdminCredentials_invokedWithNullUser_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.checkAdminCredentials(null, "pass"));
    }


    @Test
    void WHEN_checkAdminCredentials_invokedWithNullPassword_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.checkAdminCredentials("username", null));
    }


    @Test
    void WHEN_checkCredentials_invokedWithNullUserAndPassword_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.checkCredentials(null, null));
    }

    @Test
    void WHEN_addRequest_invokedWithNullUser_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.addRequest(null));
    }

    @Test
    void WHEN_deleteRequest_invokedWithNullUser_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.deleteRequest(null));
    }


    @Test
    void WHEN_deleteComment_invokedWithNullUser_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.deleteComment(null));
    }


    @Test
    void WHEN_reportReview_invokedWithNullUser_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.reportReview(null));
    }

    @Test
    void WHEN_reportUser_invokedWithNullUser_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.reportReview(null));
    }

    @Test
    void WHEN_getUser_invokedWithNullUser_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.getUser(null));
    }

    @Test
    void WHEN_getArtist_invokedWithNullUser_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.getArtist(null));
    }

    @Test
    void WHEN_getSong_invokedWithNullUser_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.getSong(null));
    }

    @Test
    void WHEN_getReviews_invokedWithNullUser_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.getReviews(null, 0, 0));
    }


    @Test
    void WHEN_getReviewsByUsername_invokedWithNullUser_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> persistenceImplementation.getReviewsByUsername(null));
    }

    @AfterEach
    void tearDown() {
        persistenceImplementation.close();
    }
}
