package it.unipi.iit.inginf.lsmdb.communitunes.persistence;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

class MongoDriver implements AutoCloseable {
    private final MongoClient mongoClient;

    MongoDriver(String connectionString){
        mongoClient = MongoClients.create(connectionString);
    }

    @Override
    public void close() {
        mongoClient.close();
    }
}
