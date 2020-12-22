package it.unipi.iit.inginf.lsmdb.communitunes.persistence;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import java.io.Closeable;

class MongoDriver implements Closeable {
    private final MongoClient mongoClient;

    MongoDriver(String connectionString){
        if(connectionString != null){
            mongoClient = MongoClients.create(connectionString);
        }
        else{
            // TODO: raise exception
            mongoClient = null;
        }
    }

    @Override
    public void close() {
        mongoClient.close();
    }
}
