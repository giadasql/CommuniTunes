package it.unipi.iit.inginf.lsmdb.communitunes.persistence;

public class PersistenceFactory {

    private PersistenceFactory(){ }
    private static Persistence persistenceInstance = null;

    public static Persistence CreatePersistence(){
        if(persistenceInstance == null){
            persistenceInstance = new PersistenceImplementation();
        }
        return persistenceInstance;
    }

}
