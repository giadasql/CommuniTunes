package it.unipi.iit.inginf.lsmdb.communitunes.persistence;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.*;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.BSONObject;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.BasicBSONList;
import org.bson.types.ObjectId;

import static com.mongodb.client.model.Aggregates.limit;
import static com.mongodb.client.model.Aggregates.project;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Updates.*;

import java.io.Closeable;
import java.lang.reflect.Array;
import java.util.*;

class MongoDriver implements Closeable {
    private final MongoClient mongoClient;
    private final MongoCollection<Document> usersCollection;
    private final MongoCollection<Document> songsCollection;
    private final MongoCollection<Document> oldReviewsCollection;

    MongoDriver(String connectionString){
        MongoDatabase database;
        if(connectionString != null){
            mongoClient = MongoClients.create(connectionString);
            database = mongoClient.getDatabase("project");
            usersCollection = database.getCollection("users");
            songsCollection = database.getCollection("songs");
            oldReviewsCollection = database.getCollection("old_reviews");
        }
        else{
            // TODO: raise exception
            mongoClient = null;
            usersCollection = null;
            songsCollection = null;
            oldReviewsCollection = null;
        }
    }

    public boolean checkIfUsernameExists(String username) {
        return usersCollection.find(eq("username", username)).first() != null;
    }

    public boolean checkIfEmailExists(String email) {
        return usersCollection.find(eq("email", email)).first() != null;
    }

    public String addUser(String username, String email, String psw) {
        Document user = new Document();
        user.append("username", username);
        user.append("email", email);
        user.append("password", psw);
        InsertOneResult insertOneResult = usersCollection.insertOne(user);
        // TODO: probabilmente non serve ritornare l'ID
        return insertOneResult.getInsertedId() == null ? null : insertOneResult.getInsertedId().asObjectId().getValue().toString();
    }

    public boolean deleteUser(String username) {
        DeleteResult deleteResult = usersCollection.deleteOne(eq("username", username));
        return deleteResult.wasAcknowledged() && deleteResult.getDeletedCount() >= 1;
    }

    public boolean deleteReviews(String username){
        BasicDBObject query = new BasicDBObject();
        BasicDBObject fields = new BasicDBObject("reviews",
                new BasicDBObject( "username", username));
        BasicDBObject update = new BasicDBObject("$pull",fields);

        UpdateResult updateRes = songsCollection.updateMany( query, update );
        return updateRes.wasAcknowledged();
    }

    public boolean checkPassword(String username, String password){
        BasicDBObject criteria = new BasicDBObject();
        criteria.append("username", username);
        criteria.append("password", password);

        return usersCollection.find(criteria).first() != null;
    }

    public Map<String, Object> getUserData(String username){
        Document user = usersCollection.find(eq("username", username)).first();
        if(user != null){
            return getUserMap(user);
        }
        return null;
    }

    public Map<String, Object> getArtistData(String username){
        Document artist = usersCollection.find(eq("username", username)).first();
        if(artist != null){
            return getArtistMap(artist);
        }
        return null;
    }

    public Map<String, Object> getSongData(String SongID){

        Bson match = Aggregates.match((eq("_id", new ObjectId(SongID))));

        BasicDBList list = new BasicDBList();
        list.add("$reviews");
        list.add(15);
        Bson slice = new BasicDBObject("reviews", new BasicDBObject("$slice", list));
        Bson project = Aggregates.project(Projections.fields(include("_id", "name", "length", "songLink", "album", "reviews", "genres", "image"), slice));

        Bson limit = limit(1);

        Document song = songsCollection.aggregate(Arrays.asList(match, limit, project)).first();
        if(song != null){
            return getSongMap(song);
        }
        return null;
    }

    // TODO: try to add timestamp
    public String addReview(String user, int rating, String text, String songID){
        Bson filter = new BasicDBObject("_id", new ObjectId(songID));

        ObjectId reviewId = new ObjectId();

        Document reviewDoc = new Document("user", user)
                .append("rating", rating)
                .append("text", text)
                .append("_id", reviewId);

        // Bson updateWithTimestamp = currentTimestamp("posted");
        Bson update = addToSet("reviews", reviewDoc);

        Document result = songsCollection.findOneAndUpdate(filter, update);
        return result != null ? reviewId.toString() : null;
    }

    public List<Map<String, Object>> getSongReviews(String songID, int nMax){
        List<Map<String, Object>> reviews = new ArrayList<>();

        Bson match = Aggregates.match((eq("_id", new ObjectId(songID))));

        BasicDBList list = new BasicDBList();
        list.add("$reviews");
        list.add(nMax);
        Bson slice = new BasicDBObject("reviews", new BasicDBObject("$slice", list));

        Bson project = Aggregates.project(Projections.fields(include("reviews"), slice));

        Bson limit = limit(1);

        Document song = songsCollection.aggregate(Arrays.asList(match, limit, project)).first();
        if(song != null){
            for (Document reviewDoc: song.getList("reviews", Document.class)) {
                Map<String, Object> reviewMap = getReviewMap(reviewDoc, song.getObjectId("_id").toString());
                reviews.add(reviewMap);
            }
        }
        return reviews;
    }

    public boolean deleteReview(String songID, String reviewID){
        Bson filter = new BasicDBObject("_id", new ObjectId(songID));
        Bson pullReview = pull("reviews", new BasicDBObject("_id", new ObjectId(reviewID)));

        Document result = songsCollection.findOneAndUpdate(filter, pullReview);
        return  result != null;
    }

    private Map<String, Object> getReviewMap(Document review, String songID){
        HashMap<String, Object> reviewMap = new HashMap<>();
        reviewMap.put("user", review.getString("user"));
        reviewMap.put("text", review.getString("text"));
        reviewMap.put("rating", review.getInteger("rating"));
        reviewMap.put("id", review.getObjectId("_id").toString());
        reviewMap.put("songID", songID);
        return reviewMap;
    }

    private Map<String, Object> getSongMap(Document song){
        if(song == null){
            return null;
        }
        HashMap<String, Object> songValues = new HashMap<>();
        songValues.put("title", song.get("name"));
        songValues.put("id", song.getObjectId("_id").toString());
        songValues.put("duration", song.get("length"));
        songValues.put("image", song.get("image"));
        songValues.put("link", song.get("songLink"));
        songValues.put("album", song.get("album"));
        List<Map<String, Object>> reviews = new ArrayList<>();
        for (Document reviewDoc: song.getList("reviews", Document.class)) {
            Map<String, Object> reviewMap = getReviewMap(reviewDoc, song.getObjectId("_id").toString());
            reviews.add(reviewMap);
        }
        songValues.put("reviews", reviews);
        songValues.put("genres", song.get("genres"));
        return songValues;
    }

    private Map<String, Object> getUserMap(Document user){
        if(user == null){
            return null;
        }
        HashMap<String, Object> userValues = new HashMap<>();
        userValues.put("username", user.get("username"));
        userValues.put("email", user.get("email"));
        userValues.put("id", user.getObjectId("_id").toString());
        userValues.put("password", user.get("password"));
        userValues.put("birthday", user.get("birthday"));
        userValues.put("country", user.get("country"));
        return userValues;
    }

    private Map<String, Object> getArtistMap(Document artist){
        if(artist == null){
            return null;
        }
        HashMap<String, Object> artistValues = new HashMap<>();
        artistValues.put("email", artist.get("email"));
        artistValues.put("username", artist.get("username"));
        artistValues.put("id", artist.getObjectId("_id").toString());
        artistValues.put("password", artist.get("password"));
        artistValues.put("birthday", artist.get("birthday"));
        artistValues.put("stageName", artist.get("stageName"));
        artistValues.put("biography", artist.get("biography"));
        artistValues.put("activity", artist.get("activity"));
        return artistValues;
    }

    @Override
    public void close() {
        mongoClient.close();
    }
}
