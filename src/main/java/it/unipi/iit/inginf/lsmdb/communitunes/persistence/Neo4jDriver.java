package it.unipi.iit.inginf.lsmdb.communitunes.persistence;

import org.neo4j.driver.*;

import java.io.Closeable;

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
                Result res = tx.run( "MATCH (u:User { username: \"$username\" }) RETURN u", parameters("username", username));
                return res.hasNext();
            });
        }
    }

    @Override
    public void close() {
        driver.close();
    }
}
