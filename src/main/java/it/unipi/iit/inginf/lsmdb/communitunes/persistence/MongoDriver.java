package it.unipi.iit.inginf.lsmdb.communitunes.persistence;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoCommandException;
import com.mongodb.client.*;
import com.mongodb.client.model.*;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.Artist;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.Song;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.User;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.SongPreview;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.BasicBSONList;
import org.bson.types.ObjectId;
import org.javatuples.Pair;

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Accumulators.*;

import javax.print.Doc;

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Updates.*;

import java.io.Closeable;
import java.lang.reflect.Array;
import java.util.*;

class MongoDriver implements Closeable {
    private final MongoClient mongoClient;
    private final MongoCollection<Document> usersCollection;
    private final MongoCollection<Document> songsCollection;
    private final MongoCollection<Document> oldReviewsCollection;

    MongoDriver(String connectionString){
        MongoDatabase database;
        if(connectionString != null){
            mongoClient = MongoClients.create(connectionString);
            database = mongoClient.getDatabase("project");
            usersCollection = database.getCollection("users");
            songsCollection = database.getCollection("songs");
            oldReviewsCollection = database.getCollection("old_reviews");
        }
        else{
            // TODO: raise exception
            mongoClient = null;
            usersCollection = null;
            songsCollection = null;
            oldReviewsCollection = null;
        }
    }

    public boolean checkIfUsernameExists(String username) {
        return usersCollection.find(eq("username", username)).first() != null;
    }

    public boolean checkIfEmailExists(String email) {
        return usersCollection.find(eq("email", email)).first() != null;
    }

    private String getIdFromUsername(String username){
        Document doc = usersCollection.find(Filters.eq("username", username)).first();
        return doc == null ? null : (String)doc.get("_id");
    }

    public String addUser(String username, String email, String psw) {
        Document user = new Document();
        user.append("username", username);
        user.append("email", email);
        user.append("password", psw);
        InsertOneResult insertOneResult = usersCollection.insertOne(user);
        // TODO: probabilmente non serve ritornare l'ID
        return insertOneResult.getInsertedId() == null ? null : insertOneResult.getInsertedId().asObjectId().getValue().toString();
    }

    public boolean deleteUser(String username) {
        DeleteResult deleteResult = usersCollection.deleteOne(eq("username", username));
        return deleteResult.wasAcknowledged() && deleteResult.getDeletedCount() >= 1;
    }

    public boolean updateUser(User newUser){
        Document query = new Document();
        query.append("_id", new ObjectId(newUser.ID));
        Document setData = new Document();
        setData.append("password", newUser.Password).append("email", newUser.Email)
                .append("country", newUser.Country).append("birthday", newUser.Birthday);
        Document update = new Document();
        update.append("$set", setData);

        UpdateResult updateRes = usersCollection.updateOne(query, update);

        return updateRes.getModifiedCount() != 0;
    }

    public String addArtist(String username, String stageName){
        Document query = new Document();
        query.append("username", username);
        Document setData = new Document();
        setData.append("stageName", stageName);
        Document update = new Document();
        update.append("$set", setData);

        UpdateResult updateRes = usersCollection.updateOne(query, update);
        String id = getIdFromUsername(username);

        return updateRes.getModifiedCount() == 0 ? null : id;
    }

        // We don't check the number of deleted songs to understand if the query was performed correctly,
        // since we may have deleted a user without songs
    public boolean deleteArtist(String username) {
        String id = getIdFromUsername(username);
        DeleteResult songsDelete = songsCollection.deleteMany(eq("artistId", id));
        DeleteResult deleteResult = usersCollection.deleteOne(eq("username", username));
        return deleteResult.wasAcknowledged() && songsDelete.wasAcknowledged() && deleteResult.getDeletedCount() >= 1;
    }

    public boolean updateArtist(Artist newArtist){
        Document query = new Document();
        query.append("_id", new ObjectId(newArtist.ID));
        Document setData = new Document();
        setData.append("password", newArtist.Password).append("email", newArtist.Email)
                .append("country", newArtist.Country).append("birthday", newArtist.Birthday)
                .append("activity", newArtist.ActiveYears).append("image", newArtist.Image)
                .append("biography", newArtist.Biography).append("stageName", newArtist.StageName);
        Document update = new Document();
        update.append("$set", setData);

        UpdateResult updateRes = usersCollection.updateOne(query, update);

        return updateRes.getModifiedCount() != 0;
    }

    public String addSong(String artist, String duration, String title, String album){
        Document song = new Document();
        song.append("name", title);
        song.append("lenght", duration);
        song.append("album", album);
        InsertOneResult insertOneResult = songsCollection.insertOne(song);
        return insertOneResult.getInsertedId() == null ? null : insertOneResult.getInsertedId().asObjectId().getValue().toString();
    }

    public boolean deleteSong(String id) {
        DeleteResult deleteResult = songsCollection.deleteOne(new Document("_id", new ObjectId(id)));
        return deleteResult.wasAcknowledged() && deleteResult.getDeletedCount() >= 1;
    }

    public boolean deleteReviews(String username){
        BasicDBObject query = new BasicDBObject();
        BasicDBObject fields = new BasicDBObject("reviews",
                new BasicDBObject( "username", username));
        BasicDBObject update = new BasicDBObject("$pull",fields);

        UpdateResult updateRes = songsCollection.updateMany( query, update );
        return updateRes.wasAcknowledged();
    }

    public boolean updateSong(Song newSong){
        Document query = new Document();
        query.append("_id", new ObjectId(newSong.ID));
        Document setData = new Document();
        setData.append("songLink", newSong.Link).append("image", newSong.Image).append("genres", newSong.Genres);
        Document update = new Document();
        update.append("$set", setData);

        UpdateResult updateRes = usersCollection.updateOne(query, update);

        return updateRes.getModifiedCount() != 0;
    }

        // TODO: Controllare che prenda l'_id della review e non quello della canzone
    public boolean updateReview(String id, int rating, String text){
        Document query = new Document();
        query.append("reviews._id", new ObjectId(id));
        Document setData = new Document();
        setData.append("rating", rating).append("text", text);
        Document update = new Document();
        update.append("$set", setData);

        UpdateResult updateRes = songsCollection.updateOne(query, update);

        return updateRes.getModifiedCount() != 0;
    }

    public HashMap<String, List<SongPreview>> getSuggestedSongs(){
        HashMap<String, List<SongPreview>> res = new HashMap<>();
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_MONTH, -30);

        Bson myMatch = match(gte("review.timestamp", cal.getTimeInMillis()));
        Bson myUnwind = unwind("genre");
        Bson myGroup = new Document("$group", new Document("_id", new Document("genre", "$genre")).
                append("songs", new Document("$push", new Document("songId", "$songId").append("title", "$title")
                .append("stageName", "$stageName").append("artistId", "$artistId").append("avgRev", new Document("$avg", "$reviews.rating")))));

        songsCollection.aggregate(Arrays.asList(myMatch, myUnwind, myGroup)).forEach(doc ->{
            String genre = doc.getString("genre");
            List<Document> list = doc.get("songs", new ArrayList<Document>().getClass());
            HashMap<SongPreview, Double> songs = new HashMap<SongPreview, Double>();
            for(Document embDoc : list){
                SongPreview song = new SongPreview(embDoc.getObjectId("songId").toString(), embDoc.getString("stageName"),
                        embDoc.getObjectId("artistId").toString(), embDoc.getString("title"));
                Double avg = embDoc.getDouble("avg");
                songs.put(song, avg);
            }

        });

        return res;
    }

    /*public List<SongPreview> getSuggestedSongs(List<Pair<String,String>> songs){
        if(songs == null)
            return null;
        List<SongPreview> res = new ArrayList<>();

        for(Pair<String,String> iter : songs){
            Bson myMatch = and(match(eq("title", iter.getValue0())), match(eq("artist", iter.getValue1())));
            Bson myProject = project(fields(include("artistId")));
            Document doc = songsCollection.aggregate(Arrays.asList(myMatch, myProject)).first();
            res.add(new SongPreview(doc.getObjectId("_id").toString(), iter.getValue1(),
                    doc.getObjectId("artistId").toString(), iter.getValue0()));
        }
        return res;
    }*/

    public boolean checkPassword(String username, String password){
        BasicDBObject criteria = new BasicDBObject();
        criteria.append("username", username);
        criteria.append("password", password);

        return usersCollection.find(criteria).first() != null;
    }

    public Map<String, Object> getUserData(String username){
        Document user = usersCollection.find(eq("username", username)).first();
        if(user != null){
            return getUserMap(user);
        }
        return null;
    }

    public Map<String, Object> getArtistData(String username){
        Document artist = usersCollection.find(eq("username", username)).first();
        if(artist != null){
            return getArtistMap(artist);
        }
        return null;
    }

    public Map<String, Object> getSongData(String SongID){

        Bson match = match((eq("_id", new ObjectId(SongID))));

        BasicDBList list = new BasicDBList();
        list.add("$reviews");
        list.add(15);
        Bson slice = new BasicDBObject("reviews", new BasicDBObject("$slice", list));
        Bson project = Aggregates.project(Projections.fields(include("_id", "name", "length", "songLink", "album", "reviews", "genres", "image"), slice));

        Bson limit = limit(1);

        Document song = songsCollection.aggregate(Arrays.asList(match, limit, project)).first();
        if(song != null){
            return getSongMap(song);
        }
        return null;
    }

    // TODO: try to add timestamp
    public String addReview(String user, int rating, String text, String songID){
        Bson filter = new BasicDBObject("_id", new ObjectId(songID));

        ObjectId reviewId = new ObjectId();

        Document doc = mongoClient.getDatabase("project").runCommand(new BasicDBObject("serverStatus", 1));

        Date current = (Date) doc.get("localTime");

        if(current != null){
            Document reviewDoc = new Document("user", user)
                    .append("rating", rating)
                    .append("text", text)
                    .append("_id", reviewId)
                    .append("posted", current);

            Bson update = Updates.addToSet("reviews", reviewDoc);

            Document result = songsCollection.findOneAndUpdate(filter, update);
            if (result != null) {
                return reviewId.toString();
            }
        }
        return null;
    }

    public List<Map<String, Object>> getSongReviews(String songID, int nMax){
        List<Map<String, Object>> reviews = new ArrayList<>();

        Bson match = match((eq("_id", new ObjectId(songID))));

        BasicDBList list = new BasicDBList();
        list.add("$reviews");
        list.add(nMax);
        Bson slice = new BasicDBObject("reviews", new BasicDBObject("$slice", list));

        Bson project = Aggregates.project(Projections.fields(include("reviews"), slice));

        Bson limit = limit(1);

        Document song = songsCollection.aggregate(Arrays.asList(match, limit, project)).first();
        if(song != null){
            for (Document reviewDoc: song.getList("reviews", Document.class)) {
                Map<String, Object> reviewMap = getReviewMap(reviewDoc, song.getObjectId("_id").toString());
                reviews.add(reviewMap);
            }
        }
        return reviews;
    }

    public boolean deleteReview(String songID, String reviewID){
        Bson filter = new BasicDBObject("_id", new ObjectId(songID));
        Bson pullReview = pull("reviews", new BasicDBObject("_id", new ObjectId(reviewID)));

        Document result = songsCollection.findOneAndUpdate(filter, pullReview);
        return  result != null;
    }

    private Map<String, Object> getReviewMap(Document review, String songID){
        HashMap<String, Object> reviewMap = new HashMap<>();
        reviewMap.put("user", review.getString("user"));
        reviewMap.put("text", review.getString("text"));
        reviewMap.put("rating", review.getInteger("rating"));
        reviewMap.put("id", review.getObjectId("_id").toString());
        reviewMap.put("songID", songID);
        return reviewMap;
    }

    private Map<String, Object> getSongMap(Document song){
        if(song == null){
            return null;
        }
        HashMap<String, Object> songValues = new HashMap<>();
        songValues.put("title", song.get("name"));
        songValues.put("id", song.getObjectId("_id").toString());
        songValues.put("duration", song.get("length"));
        songValues.put("image", song.get("image"));
        songValues.put("link", song.get("songLink"));
        songValues.put("album", song.get("album"));
        List<Map<String, Object>> reviews = new ArrayList<>();
        for (Document reviewDoc: song.getList("reviews", Document.class)) {
            Map<String, Object> reviewMap = getReviewMap(reviewDoc, song.getObjectId("_id").toString());
            reviews.add(reviewMap);
        }
        songValues.put("reviews", reviews);
        songValues.put("genres", song.get("genres"));
        return songValues;
    }

    private Map<String, Object> getUserMap(Document user){
        if(user == null){
            return null;
        }
        HashMap<String, Object> userValues = new HashMap<>();
        userValues.put("username", user.get("username"));
        userValues.put("email", user.get("email"));
        userValues.put("id", user.getObjectId("_id").toString());
        userValues.put("password", user.get("password"));
        userValues.put("birthday", user.get("birthday"));
        userValues.put("country", user.get("country"));
        return userValues;
    }

    private Map<String, Object> getArtistMap(Document artist){
        if(artist == null){
            return null;
        }
        HashMap<String, Object> artistValues = new HashMap<>();
        artistValues.put("email", artist.get("email"));
        artistValues.put("username", artist.get("username"));
        artistValues.put("id", artist.getObjectId("_id").toString());
        artistValues.put("password", artist.get("password"));
        artistValues.put("birthday", artist.get("birthday"));
        artistValues.put("stageName", artist.get("stageName"));
        artistValues.put("biography", artist.get("biography"));
        artistValues.put("activity", artist.get("activity"));
        return artistValues;
    }

    @Override
    public void close() {
        mongoClient.close();
    }
}
