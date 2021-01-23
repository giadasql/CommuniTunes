package it.unipi.iit.inginf.lsmdb.communitunes.persistence;

import com.mongodb.client.result.InsertOneResult;
import org.bson.Document;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import org.neo4j.driver.summary.ResultSummary;

import java.io.Closeable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

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
                        "RETURN u AS User, Followers, Followed, FollowedArtists, FollowerArtists, COLLECT(DISTINCT { title: likedSong.title, id: likedSong.songID  }) AS Songs", parameters("username", username));

                return res.single();
            });
            if(result != null){
                userValues.put(("followers"), result.get("Followers").asList());
                userValues.put(("followed"), result.get("Followed").asList());
                userValues.put(("followedArtists"), result.get("FollowedArtists").asList());
                userValues.put(("followerArtists"), result.get("FollowerArtists").asList());
                userValues.put(("likedSongs"), result.get("Songs").asList());
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
                        "RETURN a AS Artist, Followed, Follower, FollowedArtists, FollowerArtists, COLLECT(DISTINCT({title: s.title, id: s.songID})) AS Songs", parameters("username", username));
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
