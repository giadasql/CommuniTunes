package it.unipi.iit.inginf.lsmdb.communitunes.persistence;

import com.mongodb.client.result.InsertOneResult;
import org.bson.Document;
import org.neo4j.driver.*;

import java.io.Closeable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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

    public int addUser(String username) {
        try ( Session session = driver.session())
        {
            return session.writeTransaction(tx -> {
                Result res = tx.run( "MERGE (u:User {username: $username}) RETURN ID(u)",
                        parameters( "username", username));
                if (res.hasNext()) {
                    return res.single().get(0).asInt();
                }
                return -1;
            });
        }
    }

    @Override
    public void close() {
        driver.close();
    }
}
