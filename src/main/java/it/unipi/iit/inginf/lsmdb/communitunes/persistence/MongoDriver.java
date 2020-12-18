package it.unipi.iit.inginf.lsmdb.communitunes.persistence;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import java.io.Closeable;

class MongoDriver implements Closeable {
    private final MongoClient mongoClient;

    MongoDriver(String connectionString){
        mongoClient = MongoClients.create(connectionString);
    }



    @Override
    public void close() {
        mongoClient.close();
    }
}
