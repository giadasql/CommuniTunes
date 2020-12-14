package it.unipi.iit.inginf.lsmdb.communitunes.persistence;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;

class Neo4jDriver implements AutoCloseable {
    private final Driver driver;

    Neo4jDriver(String uri, String user, String password) {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    @Override
    public void close() {
        driver.close();
    }
}
