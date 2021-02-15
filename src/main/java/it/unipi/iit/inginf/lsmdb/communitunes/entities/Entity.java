package it.unipi.iit.inginf.lsmdb.communitunes.entities;

// TODO: forse questa classe astratta non serve
abstract class Entity {

    public String id;

    public Entity(String id) {
        this.id = id;
    }

    public Entity(Object id) {
        if(id != null){
            try{
                this.id = (String) id;
            }
            catch (ClassCastException exc){
                // TODO: log the exception
            }
        }
    }
}
