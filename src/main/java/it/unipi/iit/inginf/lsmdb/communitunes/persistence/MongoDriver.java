package it.unipi.iit.inginf.lsmdb.communitunes.persistence;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;

import static com.mongodb.client.model.Filters.*;
import java.io.Closeable;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

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

    public Dictionary<String, Object> getUserData(String username){
        Dictionary<String, Object> userValues = new Hashtable<>();
        Document user = usersCollection.find(eq("username", username)).first();
        if(user != null){
            userValues.put("username", username);
            userValues.put("email", user.get("email"));
            userValues.put("password", user.get("password"));
            userValues.put("birthday", user.get("birthday"));
            return userValues;
        }
        return null;
    }

    @Override
    public void close() {
        mongoClient.close();
    }
}
