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
import java.util.Hashtable;

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

    public Dictionary<String, Object> getUserData(String username){
        Dictionary<String, Object> userValues = new Hashtable<>();
        try ( Session session = driver.session())
        {
            Record result = session.readTransaction(tx -> {
                Result res = tx.run( "MATCH (u:User { username: $username }) " +
                                            "OPTIONAL MATCH (u)-[:FOLLOWS]->(followed:User) WHERE NOT followed:Artist\n" +
                                            "OPTIONAL MATCH (u)<-[:FOLLOWS]-(follower:User) WHERE NOT follower:Artist\n" +
                                            "OPTIONAL MATCH (u)-[:FOLLOWS]->(followedArtist:Artist)\n" +
                                            "OPTIONAL MATCH (u)<-[:FOLLOWS]-(followerArtist:Artist)\n" +
                                            "RETURN u AS User, COLLECT(DISTINCT(follower.username))[0..15] AS Followers, COLLECT(DISTINCT(followed.username))[0..15] AS Followed, COLLECT(DISTINCT(followedArtist.stageName))[0..15] AS FollowedArtists, COLLECT(DISTINCT(followerArtist))[0..15] AS FollowerArtists", parameters("username", username));

                return res.single();
            });
            if(result != null){
                userValues.put(("followers"), result.get("Followers").asList());
                userValues.put(("followed"), result.get("Followed").asList());
                userValues.put(("followedArtists"), result.get("FollowedArtists").asList());
                userValues.put(("followerArtists"), result.get("FollowerArtists").asList());
                return userValues;
            }
        }
        return null;
    }

    @Override
    public void close() {
        driver.close();
    }
}
