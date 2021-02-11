package it.unipi.iit.inginf.lsmdb.communitunes.persistence;

import com.mongodb.client.result.InsertOneResult;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.Artist;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.ArtistPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.SongPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.UserPreview;
import org.bson.Document;
import org.javatuples.Pair;
import org.javatuples.Tuple;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import org.neo4j.driver.summary.ResultSummary;
import org.neo4j.driver.types.MapAccessor;

import java.io.Closeable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

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

    public boolean updateUser(String username, String image){
        if(image == null){
            return true;
        }
        try ( Session session = driver.session())
        {
            return session.writeTransaction(tx -> {
                Result res = tx.run( "MATCH (u:User { username: $username }) SET u.image = $image RETURN count(u)",
                        parameters("username", username, "image", image));
                return (res.single().get(0).asInt() >= 1);
            });
        }
    }

    public int addArtist(String username, String stageName) {
        try ( Session session = driver.session())
        {
            return session.writeTransaction(tx -> {
                HashMap<String, Object> parameters = new HashMap<>();
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

    public boolean updateArtist(String username, String stageName, String image){
        try ( Session session = driver.session())
        {
            return session.writeTransaction(tx -> {
                HashMap<String, Object> parameters = new HashMap<>();
                parameters.put("username", username);
                parameters.put("stageName", stageName);
                parameters.put("image", image);
                Result res = tx.run( "MATCH (a:Artist { username: $username}) SET a.stageName = $stageName, a.image = $image RETURN count(a)",
                        parameters);
                return (res.single().get(0).asInt() >= 1);
            });
        }
    }

    public int addSong(String artist, String title, String songID) {
        try ( Session session = driver.session())
        {
            return session.writeTransaction(tx -> {
                HashMap<String, Object> parameters = new HashMap<String, Object>();
                parameters.put("username", artist);
                parameters.put("title", title);
                parameters.put("songID", songID);
                Result res = tx.run( "MATCH (a:Artist) WHERE a.username = $username " +
                                "CREATE (s:Song {title: $title, songID: $songID})," +
                                "(a)-[:PERFORMS {isMainArtist: true}]->(s) RETURN ID(s)",
                      parameters);
                // TODO: qui non vengono aggiunti i feat
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

    // TODO: Non fa l'update del titolo
    public boolean updateSong(String title, String artist, List<ArtistPreview> Featurings, String image){
        try ( Session session = driver.session())
        {
            return session.writeTransaction(tx -> {
                HashMap<String, Object> parameters = new HashMap<>();
                parameters.put("username", artist);
                parameters.put("title", title);
                parameters.put("artists", Featurings.stream().map(x->x.username).collect(Collectors.toList()));
                parameters.put("image", image);
                Result res = tx.run( "MATCH (a:Artist {username: $username})-[:PERFORMS {isMainArtist: true}]->(s:Song {title: $title}) SET s.image = $image," +
                                "MATCH (a2:Artist) WHERE ar.username in $artists\n" +
                                "MERGE (a2)-[:PERFORMS {isMainArtist: false]->(s)\n" +
                                "RETURN count(s)",
                        parameters);
                return (res.single().get(0).asInt() >= 1);
            });
        }
    }

    public List<UserPreview> getFollowedUsers(String username){
        List<UserPreview> users = new ArrayList<>();
        try ( Session session = driver.session())
        {
            return session.writeTransaction(tx -> {
                Result res = tx.run( "MATCH (u:User {username: $username})-[:FOLLOWS]->(m:User)" +
                                "RETURN m.username AS User, m.image AS Image",
                        parameters("username", username));
                while(res.hasNext()){
                    Record r = res.next();
                    users.add(new UserPreview(r.get("User").asString(), r.get("Image").asString()));
                }
                return users;
            });
        }
    }

    public List<UserPreview> getFollowers(String username){
        List<UserPreview> users = new ArrayList<>();
        try ( Session session = driver.session())
        {
            return session.writeTransaction(tx -> {
                Result res = tx.run( "MATCH (u:User {username: $username})<-[:FOLLOWS]-(m:User)" +
                                "RETURN m.username AS User, m.image AS Image",
                        parameters("username", username));
                while(res.hasNext()){
                    Record r = res.next();
                    users.add(new UserPreview(r.get("User").asString(), r.get("Image").asString()));
                }
                return users;
            });
        }
    }

    public List<SongPreview> getLikedSongs(String username){
        List<SongPreview> songs = new ArrayList<>();
        try ( Session session = driver.session())
        {
            return session.writeTransaction(tx -> {
                Result res = tx.run( "MATCH (u:User {username: $username})-[:LIKES]->(s:Song)<-[:PERFORMS {isMainArtist: true}]-(a:Artist)" +
                                "RETURN s.title AS title, a.username AS artistUsername, s.songID AS ID, s.image AS Image",
                        parameters("username", username));
                while(res.hasNext()){
                    Record r = res.next();
                    songs.add(new SongPreview(r.get("songID").asString(),
                            r.get("artistUsername").asString(), r.get("title").asString(), r.get("Image").asString()));
                }
                return songs;
            });
        }
    }

    public List<SongPreview> getArtistSongs(String username){
        List<SongPreview> songs = new ArrayList<>();
        try ( Session session = driver.session())
        {
            return session.writeTransaction(tx -> {
                Result res = tx.run( "MATCH (s:Song)<-[:PERFORMS {isMainArtist: true}]-(a:Artist {username: $username})" +
                                "RETURN s.title AS title, a.username AS artistUsername, s.songID AS ID, s.image AS Image",
                        parameters("username", username));
                while(res.hasNext()){
                    Record r = res.next();
                    songs.add(new SongPreview(r.get("songID").asString(),
                            r.get("artistUsername").asString(), r.get("title").asString(), r.get("Image").asString()));
                }
                return songs;
            });
        }
    }

    public List<SongPreview> getSuggestedSongs(String username){
        List<SongPreview> songs = new ArrayList<>();
        try ( Session session = driver.session())
        {
            return session.writeTransaction(tx -> {
                Result res = tx.run( "MATCH (u:User {username: $username})-[:FOLLOWS]->(f:User) WHERE NOT f:Artist," +
                                "(f)-[:LIKES]->(s:Song)<-[:PERFORMS {isMainArtist: true}]-(a:Artist)" +
                                "WITH COLLECT(DISTINCT(s.title))[0..15] AS Songs, a " +
                                "RETURN Songs.title AS title, a.username AS artistUsername, Songs.songID AS ID, Songs.image AS Image",
                        parameters("username", username));
                while(res.hasNext()){
                    Record r = res.next();
                    songs.add(new SongPreview(r.get("songID").asString(),
                            r.get("artistUsername").asString(), r.get("title").asString(), r.get("Image").asString()));
                }
                return songs;
            });
        }
    }

    public List<ArtistPreview> getSuggestedArtists(String username){
        List<ArtistPreview> artists = new ArrayList<>();
        try ( Session session = driver.session())
        {
            return session.writeTransaction(tx -> {
                Result res = tx.run( "MATCH (u:User {username: $username})-[:FOLLOWS]->(a:Artist)," +
                                "(a)-[:PERFORMS]->(:Song)<-[:PERFORMS]-(f:Artist) WHERE NOT a.username = f.username" +
                                "RETURN f.username AS username, f.stageName AS stageName, f.image AS Image LIMIT 15",
                        parameters("username", username));
                while(res.hasNext()){
                    Record r = res.next();
                    artists.add(new ArtistPreview(r.get("username").asString(), r.get("Image").asString()));
                }
                return artists;
            });
        }
    }

    public List<UserPreview> getSuggestedUsers(String username){
        List<UserPreview> users = new ArrayList<>();
        try ( Session session = driver.session())
        {
            return session.writeTransaction(tx -> {
                Result res = tx.run( "MATCH (u:User {username: $username})-[:FOLLOWS]->(m:User) WHERE NOT m:Artist," +
                                "(m)-[:FOLLOWS]->(f:User) WHERE NOT f:Artist AND NOT f.username = u.username" +
                                "RETURN f.username AS User, f.image AS Image LIMIT 15",
                        parameters("username", username));
                while(res.hasNext()){
                    Record r = res.next();
                    users.add(new UserPreview(r.get("User").asString(), r.get("Image").asString()));
                }
                return users;
            });
        }
    }
    //  In this function, we find all the users (that are not artists) that like a song our target user likes. Then, we
    //  count how many songs our user likes and how many songs in common our target user and the other user like. We then
    // choose as like-minded users those that like 60% or more of the songs that our target user likes.
    public List<UserPreview> getLikeMindedUsers(String username){
        List<UserPreview> users = new ArrayList<>();
        try ( Session session = driver.session())
        {
            return session.writeTransaction(tx -> {
                Result res = tx.run( "MATCH (u:User {username: $username})-[:LIKES]->(:Song)<-[:LIKES]-(f:User) WHERE NOT f:Artist," +
                                "(u)-[:LIKES]->(s1:Song)" +
                                "WITH u, f, COUNT(DISTINCT s1) AS s1Count" +
                                "MATCH (u)-[:LIKES]->(s:Song)<-[:LIKES]-(f)" +
                                "WITH f,s1Count, COUNT(DISTINCT s) AS commonSongsCount" +
                                "WHERE commonSongsCount >= s1Count * 0.6" +
                                "RETURN f.username AS User, f.image AS Image LIMIT 15 ",
                        parameters("username", username));
                while(res.hasNext()){
                    Record r = res.next();
                    users.add(new UserPreview(r.get("User").asString(), r.get("Image").asString()));
                }
                return users;
            });
        }
    }

    public List<SongPreview> getLikeMindedSongs(String username){
        List<SongPreview> songs = new ArrayList<>();
        try ( Session session = driver.session())
        {
            return session.writeTransaction(tx -> {
                Result res = tx.run( "MATCH (u:User {username: $username})-[:LIKES]->(:Song)<-[:LIKES]-(f:User) WHERE NOT f:Artist," +
                                "(u)-[:LIKES]->(s1:Song), (f)-[:LIKES]->(s2:Song)" +
                                "WITH u, f, s2, COUNT(DISTINCT s1) AS s1Count" +
                                "MATCH (u)-[:LIKES]->(s:Song)<-[:LIKES]-(f)" +
                                "WITH s1Count,s2, COUNT(DISTINCT s) AS commonSongsCount" +
                                "WHERE commonSongsCount >= s1Count * 0.6" +
                                "RETURN s2.title AS title, a.username AS artistUsername, s2.songID AS ID, s2.image AS Image LIMIT 15",
                        parameters("username", username));
                while(res.hasNext()){
                    Record r = res.next();
                    songs.add(new SongPreview(r.get("songID").asString(),
                            r.get("artistUsername").asString(), r.get("title").asString(), r.get("Image").asString()));
                }
                return songs;
            });
        }
    }

    public List<UserPreview> getTopFans(String username){
        List<UserPreview> users = new ArrayList<>();
        try ( Session session = driver.session())
        {
            return session.writeTransaction(tx -> {
                Result res = tx.run( "MATCH (a:Artist {username: $username})<-[:FOLLOWS]-(u:User) WHERE NOT u:Artist," +
                                "(a)-[:PERFORMS {isMainArtist: true}]->(s:Song)" +
                                "WITH u, s, COUNT(DISTINCT s) AS totalSongs" +
                                "MATCH (s)<-[:LIKES]-(u)" +
                                "WITH u, totalSongs, COUNT(DISTINCT s) AS likedSongs" +
                                "WHERE likedSongs >= totalSongs * 0.6" +
                                "RETURN u.username AS User, u.image AS Image LIMIT 15",
                        parameters("username", username));
                while(res.hasNext()){
                    Record r = res.next();
                    users.add(new UserPreview(r.get("User").asString(), r.get("Image").asString()));
                }
                return users;
            });
        }
    }

    public List<ArtistPreview> getSimilarArtists(String username){
        List<ArtistPreview> artists = new ArrayList<>();
        try ( Session session = driver.session())
        {
            return session.writeTransaction(tx -> {
                Result res = tx.run( "MATCH (a:Artist {username: $username})<-[:FOLLOWS]-(u:User) WHERE NOT u:Artist" +
                                "WITH u, a, COUNT(DISTINCT u) AS totFollowers" +
                                "MATCH (u)-[:FOLLOWS]->(a1:Artist) WHERE NOT a1.username = a.username" +
                                "WITH u, a, totFollowers, a1, COUNT(DISTINCT u) AS followOthers" +
                                "WHERE followOthers >= totFollowers * 0.3" +
                                "RETURN a1.username AS username, a1.stageName AS stageName, a1.image AS Image LIMIT 15",
                        parameters("username", username));
                while(res.hasNext()){
                    Record r = res.next();
                    artists.add(new ArtistPreview(r.get("username").toString(), r.get("Image").asString()));
                }
                return artists;
            });
        }
    }

    public List<SongPreview> getPopularSongs(String username){
        List<SongPreview> songs = new ArrayList<>();
        try ( Session session = driver.session())
        {
            return session.writeTransaction(tx -> {
                Result res = tx.run( "MATCH (a:Artist {username: $username})-[:PERFORMS {isMainArtist: true}]->(s:Song)," +
                                "(s)<-[:LIKES]-(u:User) WHERE NOT u:Artist AND NOT u.username = a.username" +
                                "WITH s, a, u, COUNT(DISTINCT u) AS totalLikes" +
                                "MATCH (u), (s), (a)" +
                                "WITH u,s,a,totalLikes, COUNT(DISTINCT u) AS externalLikes" +
                                "WHERE NOT (u)-[:FOLLOWS]->(a) AND (u)-[:LIKES]->(s)" +
                                "MATCH (s)" +
                                "WHERE externalLikes >= totalLikes * 0.3" +
                                "RETURN s.songID AS songID, s.title AS title, s.image AS Image LIMIT 15",
                        parameters("username", username));
                while(res.hasNext()){
                    Record r = res.next();
                    songs.add(new SongPreview(r.get("songID").asString(),
                            username, r.get("title").asString(), r.get("Image").asString()));
                }
                return songs;
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
                        "WITH COLLECT(DISTINCT(followed { .username, .image }))[0..6] AS Followed, u\n" +
                        "OPTIONAL MATCH (u)<-[:FOLLOWS]-(follower:User) WHERE NOT follower:Artist\n" +
                        "WITH COLLECT(DISTINCT(follower { .username, .image }))[0..6] AS Followers, Followed, u\n" +
                        "OPTIONAL MATCH (u)-[:FOLLOWS]->(followedArtist:Artist)\n" +
                        "WITH COLLECT(DISTINCT(followedArtist {.username, .image, .stageName}))[0..6] AS FollowedArtists, Followers, Followed, u\n" +
                        "OPTIONAL MATCH (u)<-[:FOLLOWS]-(followerArtist:Artist)\n" +
                        "WITH COLLECT(DISTINCT(followerArtist {.username, .image, .stageName}))[0..6] AS FollowerArtists, FollowedArtists, Followers, Followed, u\n" +
                        "OPTIONAL MATCH (u)-[:LIKES]->(s:Song)<-[:PERFORMS {isMainArtist: true}]-(performer:Artist)\n" +
                        "RETURN u AS User, Followers, Followed, FollowedArtists, FollowerArtists, COLLECT(DISTINCT( s { .title, .image, id: s.songID, artist: performer.username}))[0..6] AS LikedSongs", parameters("username", username));

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
                        "WITH COLLECT(DISTINCT(followed { .username, .image }))[0..6] AS Followed, a\n" +
                        "OPTIONAL MATCH (a)<-[:FOLLOWS]-(follower:User) WHERE NOT follower:Artist\n" +
                        "WITH COLLECT(DISTINCT (follower { .username, .image }))[0..6] AS Follower, Followed, a\n" +
                        "OPTIONAL MATCH (a)-[:FOLLOWS]->(followedArtist:Artist)\n" +
                        "WITH COLLECT(DISTINCT (followedArtist {.username, .image, .stageName}))[0..6] AS FollowedArtists, Follower, Followed, a\n" +
                        "OPTIONAL MATCH (followerArtist:Artist)-[:FOLLOWS]->(a)\n" +
                        "WITH COLLECT(DISTINCT (followerArtist {.username, .image, .stageName}))[0..6] AS FollowerArtists, FollowedArtists, Follower, Followed, a\n" +
                        "OPTIONAL MATCH (a)-[:PERFORMS {isMainArtist: true}]->(s:Song)\n" +
                        "WITH COLLECT(DISTINCT({id: s.songID, title: s.title, image: s.image, artist: a.username}))[0..6] AS Songs, FollowerArtists, FollowedArtists, Follower, Followed, a\n" +
                        "OPTIONAL MATCH (a)-[:LIKES]->(s:Song)<-[:PERFORMS {isMainArtist: true}]-(performer:Artist)\n" +
                        "RETURN Followed, Follower, FollowedArtists, FollowerArtists, Songs, COLLECT(DISTINCT( s { .title, .image, id: s.songID, artist: performer.username}))[0..6] AS LikedSongs", parameters("username", username));
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
                        "WITH mainArtist {.username, .image, .stageName} AS MainArtist, s\n" +
                        "LIMIT 1\n" +
                        "OPTIONAL MATCH (s)<-[r2:PERFORMS {isMainArtist: false}]-(featuring:Artist)\n" +
                        "WITH COLLECT(DISTINCT featuring {.username, .image, .stageName})[0..15] AS Featurings, MainArtist, s\n" +
                        "OPTIONAL MATCH (s)<-[:LIKES]-(u:User)\n" +
                        "RETURN s AS Song, MainArtist, Featurings, COUNT(DISTINCT(u)) AS Likers", parameters("songID", songID));
                if(res != null && res.hasNext()){
                    return res.single();
                }
                else{
                    return null;
                }
            });
            if(result != null){
                songValues.put(("mainArtist"), result.get("MainArtist").asMap());
                songValues.put(("featurings"), result.get("Featurings").asList());
                songValues.put(("likes"), result.get("Likers").asInt());
                return songValues;
            }
        }
        return songValues;
    }

    public boolean addFollow(String followed, String follower){
        try ( Session session = driver.session())
        {
            return session.writeTransaction(tx -> {
                Result res = tx.run( "MATCH (u1:User {username: $follower})\n" +
                        "MATCH (u2:User {username: $followed})\n" +
                        "MERGE (u1)-[:FOLLOWS]->(u2)",
                        parameters("followed", followed, "follower", follower));
                return true;
            });
        }
    }

    public boolean checkFollow(String followed, String follower){
        try ( Session session = driver.session())
        {
            return session.writeTransaction(tx -> {
                Result res = tx.run( "MATCH (u1:User {username: $follower})\n" +
                                "MATCH (u2:User {username: $followed})\n" +
                                "MATCH (u1)-[r:FOLLOWS]->(u2)\n" +
                                "RETURN COUNT(r)",
                        parameters("followed", followed, "follower", follower));
                return (res.single().get(0).asInt() >= 1);
            });
        }
    }

    public boolean deleteFollow(String followed, String follower){
        try ( Session session = driver.session())
        {
            return session.writeTransaction(tx -> {
                Result res = tx.run( "MATCH (u1:User {username: $follower})\n" +
                                "MATCH (u2:User {username: $followed})\n" +
                                "MATCH (u1)-[r:FOLLOWS]->(u2)\n" +
                                "DELETE r",
                        parameters("followed", followed, "follower", follower));
                return true;
            });
        }
    }

    public boolean addLike(String user, String song){
        try ( Session session = driver.session())
        {
            return session.writeTransaction(tx -> {
                Result res = tx.run( "MATCH (u1:User {username: $user})\n" +
                                "MATCH (s:Song {songID: $song})\n" +
                                "MERGE (u1)-[r:LIKES]->(s)",
                        parameters("song", song, "user", user));
                return true;
            });
        }
    }

    public boolean checkLike(String user, String song){
        try ( Session session = driver.session())
        {
            return session.writeTransaction(tx -> {
                Result res = tx.run( "MATCH (u1:User {username: $user})\n" +
                                "MATCH (s:Song {songID: $song})\n" +
                                "MATCH (u1)-[r:LIKES]->(s)\n" +
                                "RETURN COUNT(r)",
                        parameters("song", song, "user", user));
                return (res.single().get(0).asInt() >= 1);
            });
        }
    }

    public boolean deleteLike(String user, String song){
        try ( Session session = driver.session())
        {
            return session.writeTransaction(tx -> {
                Result res = tx.run( "MATCH (u1:User {username: $user})\n" +
                                "MATCH (s:Song {songID: $song})\n" +
                                "MATCH (u1)-[r:LIKES]->(s)\n" +
                                "DELETE r",
                        parameters("song", song, "user", user));
                return true;
            });
        }
    }

    @Override
    public void close() {
        driver.close();
    }
}
