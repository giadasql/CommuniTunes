package it.unipi.iit.inginf.lsmdb.communitunes.entities;

// TODO: forse questa classe astratta non serve
abstract class Entity {

    public String ID;

    public Entity(String ID) {
        this.ID = ID;
    }

    public Entity(Object ID) {
        if(ID != null){
            try{
                this.ID = (String)ID;
            }
            catch (ClassCastException exc){
                // TODO: log the exception
            }
        }
    }
}
