package it.unipi.iit.inginf.lsmdb.communitunes.utilities.exceptions;

public class PersistenceInconsistencyException extends Exception {

    public PersistenceInconsistencyException() {
        super("Due to an unexpected error, the database now contains inconsistent data.");
    }

    public PersistenceInconsistencyException(String message)
    {
        super(message);
    }
}
