package it.unipi.iit.inginf.lsmdb.communitunes.persistence;

import com.mongodb.client.result.InsertOneResult;
import org.bson.Document;
import org.javatuples.Pair;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import org.neo4j.driver.summary.ResultSummary;
import org.neo4j.driver.types.MapAccessor;

import java.io.Closeable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static com.mongodb.client.model.Filters.eq;
import static org.neo4j.driver.Values.parameters;

class Neo4jDriver implements Closeable {
    private final Driver driver;

    // TODO: mettere il giusto database nelle sessioni

    Neo4jDriver(String uri, String user, String password) {
        if(uri == null || user == null){
            // TODO: raise exception
            driver = null;
        }
        else{
            try{
                driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
            }
            catch (IllegalArgumentException exc){
                // TODO: log
                throw(exc);
            }
        }
    }

    public boolean checkIfUsernameExists(String username){
        try ( Session session = driver.session())
        {
            return session.readTransaction(tx -> {
                Result res = tx.run( "MATCH (u:User { username: $username }) RETURN u", parameters("username", username));
                return res.hasNext();
            });
        }
    }

    public int addUser(String username) {
        try ( Session session = driver.session())
        {
            return session.writeTransaction(tx -> {
                Result res = tx.run( "MERGE (u:User {username: $username}) RETURN ID(u)",
                        parameters( "username", username));
                if (res.hasNext()) {
                    // TODO: probabilmente non serve ritornare l'ID
                    return res.single().get(0).asInt();
                }
                return -1;
            });
        }
    }

    public boolean deleteUser(String username){
        try ( Session session = driver.session())
        {
            return session.writeTransaction(tx -> {
                Result res = tx.run( "MATCH (u:User { username: $username }) DETACH DELETE u RETURN count(u)", parameters("username", username));
                return (res.single().get(0).asInt() >= 1);
            });
        }
    }

    public int addArtist(String username, String stageName) {
        try ( Session session = driver.session())
        {
            return session.writeTransaction(tx -> {
                HashMap<String, Object> parameters = new HashMap<String, Object>();
                parameters.put("username", username);
                parameters.put("stageName", stageName);
                Result res = tx.run( "MATCH (u:User {username: $username}) SET u:Artist, u.stageName = $stageName  RETURN ID(u)",
                        parameters);
                if (res.hasNext()) {
                    // TODO: probabilmente non serve ritornare l'ID
                    return res.single().get(0).asInt();
                }
                return -1;
            });
        }
    }

    public boolean deleteArtist(String username){
        try ( Session session = driver.session())
        {
            return session.writeTransaction(tx -> {
                Result res = tx.run( "MATCH (a:Artist { username: $username })-[:PERFORMS {isMainArtist: true}]->(s:Song)" +
                        "DETACH DELETE s, a RETURN count(a)", parameters("username", username));
                return (res.single().get(0).asInt() >= 1);
            });
        }
    }

    public boolean updateArtist(String username, String stageName){
        try ( Session session = driver.session())
        {
            return session.writeTransaction(tx -> {
                HashMap<String, Object> parameters = new HashMap<String, Object>();
                parameters.put("username", username);
                parameters.put("stageName", stageName);
                Result res = tx.run( "MATCH (a:Artist { username: $username}) SET a.stageName = $stageName RETURN count(a)",
                        parameters);
                return (res.single().get(0).asInt() >= 1);
            });
        }
    }

    public int addSong(String artist, String title) {
        try ( Session session = driver.session())
        {
            return session.writeTransaction(tx -> {
                HashMap<String, Object> parameters = new HashMap<String, Object>();
                parameters.put("username", artist);
                parameters.put("title", title);
                Result res = tx.run( "MATCH (a:Artist) WHERE a.username = $username " +
                                "CREATE (s:Song {title: $title})," +
                                "(a)-[:PERFORMS {isMainArtist: true}]->(s) RETURN ID(s)",
                      parameters);
                if (res.hasNext()) {
                    // TODO: probabilmente non serve ritornare l'ID
                    return res.single().get(0).asInt();
                }
                return -1;
            });
        }
    }

    public boolean deleteSong(String artist, String title){
        try ( Session session = driver.session())
        {
            return session.writeTransaction(tx -> {
                HashMap<String, Object> parameters = new HashMap<String, Object>();
                parameters.put("username", artist);
                parameters.put("title", title);
                Result res = tx.run( "MATCH (a:Artist { username: $username })-[:PERFORMS {isMainArtist: true}]->(s:Song {title: $title})" +
                        "DETACH DELETE s RETURN count(s)", parameters);
                return (res.single().get(0).asInt() >= 1);
            });
        }
    }

    public boolean updateSong(String title, String artist, List<Pair<String, String>> Featurings){
        try ( Session session = driver.session())
        {
            return session.writeTransaction(tx -> {
                HashMap<String, Object> parameters = new HashMap<String, Object>();
                parameters.put("username", artist);
                parameters.put("title", title);
                Result res = tx.run( "MATCH (a:Artist {username: $username})-[:PERFORMS {isMainArtist: true}]->(s:Song {title: $title})," +
                                "FOREACH(ar IN {$artists} |" +
                                "MERGE (s)<-[:PERFORMS {isMainArtist: false}]-(ar:Artist",
                        parameters);
                return (res.single().get(0).asInt() >= 1);
            });
        }
    }

    public Map<String, Object> getUserData(String username){
        Map<String, Object> userValues = new HashMap<>();
        try ( Session session = driver.session())
        {
            Record result = session.readTransaction(tx -> {
                Result res = tx.run( "MATCH (u:User { username: $username }) \n" +
                        "OPTIONAL MATCH (u)-[:FOLLOWS]->(followed:User) WHERE NOT followed:Artist\n" +
                        "WITH COLLECT(DISTINCT(followed.username))[0..15] AS Followed, u\n" +
                        "OPTIONAL MATCH (u)<-[:FOLLOWS]-(follower:User) WHERE NOT follower:Artist\n" +
                        "WITH COLLECT(DISTINCT(follower.username))[0..15] AS Followers, Followed, u\n" +
                        "OPTIONAL MATCH (u)-[:FOLLOWS]->(followedArtist:Artist)\n" +
                        "WITH COLLECT(DISTINCT(followedArtist.username))[0..15] AS FollowedArtists, Followers, Followed, u\n" +
                        "OPTIONAL MATCH (u)<-[:FOLLOWS]-(followerArtist:Artist)\n" +
                        "WITH COLLECT(DISTINCT(followerArtist.username))[0..15] AS FollowerArtists, FollowedArtists, Followers, Followed, u\n" +
                        "OPTIONAL MATCH (u)-[:LIKES]->(likedSong:Song)\n" +
                        "RETURN u AS User, Followers, Followed, FollowedArtists, FollowerArtists, COLLECT(DISTINCT(likedSong { .songID, .title }))[0..15] AS LikedSongs", parameters("username", username));

                return res.single();
            });
            if(result != null){
                userValues.put(("followers"), result.get("Followers").asList());
                userValues.put(("followed"), result.get("Followed").asList());
                userValues.put(("followedArtists"), result.get("FollowedArtists").asList());
                userValues.put(("followerArtists"), result.get("FollowerArtists").asList());
                userValues.put(("likes"), result.get("LikedSongs").asList());
                return userValues;
            }
        }
        return null;
    }

    public Map<String, Object> getArtistData(String username){
        Map<String, Object> artistValues = new HashMap<>();
        try ( Session session = driver.session())
        {
            Record result = session.readTransaction(tx -> {
                Result res = tx.run( "MATCH (a:Artist { username: $username })\n" +
                        "OPTIONAL MATCH (a)-[:FOLLOWS]->(followed:User) WHERE NOT followed:Artist\n" +
                        "WITH COLLECT(DISTINCT followed.username)[0..15] AS Followed, a\n" +
                        "OPTIONAL MATCH (a)<-[:FOLLOWS]-(follower:User) WHERE NOT follower:Artist\n" +
                        "WITH COLLECT(DISTINCT follower.username)[0..15] AS Follower, Followed, a\n" +
                        "OPTIONAL MATCH (a)-[:FOLLOWS]->(followedArtist:Artist)\n" +
                        "WITH COLLECT(DISTINCT followedArtist.stageName)[0..15] AS FollowedArtists, Follower, Followed, a\n" +
                        "OPTIONAL MATCH (followerArtist:Artist)-[:FOLLOWS]->(a)\n" +
                        "WITH COLLECT(DISTINCT followerArtist.stageName)[0..15] AS FollowerArtists, FollowedArtists, Follower, Followed, a\n" +
                        "OPTIONAL MATCH (a)-[:PERFORMS]->(s:Song)\n" +
                        "WITH COLLECT(DISTINCT(s { .songID, .title }))[0..15] AS Songs, FollowerArtists, FollowedArtists, Follower, Followed, a\n" +
                        "OPTIONAL MATCH (a)-[:LIKES]->(likedSong:Song)\n" +
                        "RETURN Followed, Follower, FollowedArtists, FollowerArtists, Songs, COLLECT(DISTINCT(likedSong { .songID, .title }))[0..15] AS LikedSongs", parameters("username", username));
                if(res != null && res.hasNext()){
                    return res.single();
                }
                else{
                    return null;
                }
            });
            if(result != null){
                artistValues.put(("followers"), result.get("Follower").asList());
                artistValues.put(("followed"), result.get("Followed").asList());
                artistValues.put(("followedArtists"), result.get("FollowedArtists").asList());
                artistValues.put(("followerArtists"), result.get("FollowerArtists").asList());
                artistValues.put(("songs"), result.get("Songs").asList());
                artistValues.put(("likes"), result.get("LikedSongs").asList());
                return artistValues;
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, Object> getSongData(String songID){
        Map<String, Object> songValues = new HashMap<>();
        try ( Session session = driver.session())
        {
            Record result = session.readTransaction(tx -> {
                Result res = tx.run( "MATCH (s:Song { songID: $songID  } )\n" +
                        "OPTIONAL MATCH (s)<-[r:PERFORMS {isMainArtist: true} ]-(mainArtist:Artist)\n" +
                        "WITH mainArtist.username AS MainArtist, s\n" +
                        "LIMIT 1\n" +
                        "OPTIONAL MATCH (s)<-[r2:PERFORMS {isMainArtist: false}]-(featuring:Artist)\n" +
                        "WITH COLLECT(DISTINCT featuring.username)[0..15] AS Featurings, MainArtist, s\n" +
                        "OPTIONAL MATCH (s)<-[:LIKES]-(u:User)\n" +
                        "RETURN s AS Song, MainArtist, Featurings, COLLECT(DISTINCT u.username)[0..15] AS Likers", parameters("songID", songID));
                if(res != null && res.hasNext()){
                    return res.single();
                }
                else{
                    return null;
                }
            });
            if(result != null){
                songValues.put(("mainArtist"), result.get("MainArtist").asString());
                songValues.put(("featurings"), result.get("Featurings").asList());
                songValues.put(("likers"), result.get("Likers").asList());
                return songValues;
            }
        }
        return songValues;
    }

    @Override
    public void close() {
        driver.close();
    }
}
