package it.unipi.iit.inginf.lsmdb.communitunes.persistence;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import static com.mongodb.client.model.Filters.*;

import java.io.Closeable;

class MongoDriver implements Closeable {
    private final MongoClient mongoClient;
    private final MongoCollection<Document> usersCollection;
    private final MongoCollection<Document> songsCollection;

    MongoDriver(String connectionString){
        MongoDatabase database;
        if(connectionString != null){
            mongoClient = MongoClients.create(connectionString);
            database = mongoClient.getDatabase("project");
            usersCollection = database.getCollection("users");
            songsCollection = database.getCollection("songs");
        }
        else{
            // TODO: raise exception
            mongoClient = null;
            usersCollection = null;
            songsCollection = null;
        }
    }

    public boolean checkIfUsernameExists(String username){
        return usersCollection.find(eq("username", username)).first() != null;
    }

    public boolean checkIfEmailExists(String email){
        return usersCollection.find(eq("email", email)).first() != null;
    }

    @Override
    public void close() {
        mongoClient.close();
    }
}
