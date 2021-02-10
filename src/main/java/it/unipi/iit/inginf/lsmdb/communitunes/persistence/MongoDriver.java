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
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.ArtistPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.SongPreview;
import org.bson.*;
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
import static com.mongodb.client.model.Sorts.descending;
import static com.mongodb.client.model.Updates.*;

import java.io.Closeable;
import java.lang.reflect.Array;
import java.util.*;

class MongoDriver implements Closeable {
    private final MongoClient mongoClient;
    private final MongoCollection<Document> usersCollection;
    private final MongoCollection<Document> songsCollection;

    MongoDriver(String connectionString){
        MongoDatabase database;
        if(connectionString != null){
            mongoClient = MongoClients.create(connectionString);
            database = mongoClient.getDatabase("project");
            usersCollection = database.getCollection("users");
            songsCollection = database.getCollection("songs");
        }
        else{
            // TODO: raise exception
            mongoClient = null;
            usersCollection = null;
            songsCollection = null;
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

    // TODO: check if a field is null. If it is, remove it from the document
    public boolean updateUser(User newUser){
        Document query = new Document();
        query.append("_id", new ObjectId(newUser.ID));
        Document setData = new Document();
        setData.append("password", newUser.Password).append("email", newUser.Email)
                .append("country", newUser.Country).append("birthday", newUser.Birthday)
                .append("first_name", newUser.FirstName).append("last_name", newUser.LastName)
                .append("image", newUser.Image);
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

    // TODO: check if a field is null. If it is, remove it from the document
    public boolean updateArtist(Artist newArtist){
        Document query = new Document();
        query.append("_id", new ObjectId(newArtist.ID));
        Document setData = new Document();
        setData.append("password", newArtist.Password).append("email", newArtist.Email)
                .append("country", newArtist.Country).append("birthday", newArtist.Birthday)
                .append("activity", newArtist.ActiveYears).append("image", newArtist.Image)
                .append("biography", newArtist.Biography).append("stageName", newArtist.StageName)
                .append("first_name", newArtist.FirstName).append("last_name", newArtist.LastName);
        Document update = new Document();
        update.append("$set", setData);

        UpdateResult updateRes = usersCollection.updateOne(query, update);

        return updateRes.getModifiedCount() != 0;
    }

    public String addSong(String artist, String duration, String title, String album){
        Document song = new Document();
        song.append("artist", artist);
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

    // TODO: check if a field is null. If it is, remove it from the document
    public boolean updateSong(Song newSong){
        Document query = new Document();
        query.append("_id", new ObjectId(newSong.ID));
        Document setData = new Document();
        setData.append("links", newSong.Links)
                .append("image", newSong.Image)
                .append("genres", newSong.Genres);
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

        songsCollection.aggregate(Arrays.asList(addFields(new Field("reviews",
                new Document("$filter",
                        new Document("input", "$reviews")
                                .append("as", "review")
                                .append("cond",
                                        new Document("$and", Arrays.asList(new Document("$gt", Arrays.asList(new java.util.Date(), "$$review.posted")),
                                                new Document("$lte", Arrays.asList(new java.util.Date(), "$$review.posted")))))))), addFields(new Field("avg_rating",
                new Document("$avg", "$reviews.rating"))), match(ne("avg_rating",
                new BsonNull())), sort(descending("avg_rating")), unwind("$genres",
                new UnwindOptions().preserveNullAndEmptyArrays(false)), group("$genres", Accumulators.push("songs", and(eq("name", "$title"), eq("avg_rating", "$avg_rating"), eq("artist", "$artist"), eq("id", "$_id"), eq("image", "$image")))), addFields(new Field("songs",
                new Document("$slice", Arrays.asList("$songs", 6L))))))
                .forEach(doc->{
                    String genre = doc.getString("genres");
                });

        return res;
    }

    public HashMap<String, String> getApprAlbum(String username){
        HashMap<String, String> res = new HashMap<>();

        Document query = songsCollection.aggregate(Arrays.asList(match(eq("artist", username)), addFields(new Field("avg_song_rating",
                new Document("$avg", "$reviews.rating"))), match(ne("avg_song_rating",
                new BsonNull())), group("$album", avg("avg_album_rating", "$avg_song_rating")), sort(descending("avg_album_rating")), group("Album stats", first("best_album", "$_id"), last("worst_album", "$_id")))).first();

        res.put("best", query.getString("best_album"));
        res.put("worst", query.getString("worst_album"));

        return res;
    }

    public List<Pair<String, ArtistPreview>> getRepresentativeArtist(){
        List<Pair<String, ArtistPreview>> res = new ArrayList<>();

        usersCollection.aggregate(Arrays.asList(addFields(new Field("avg_song_rating",
                new Document("$avg", "$reviews.rating"))), unwind("$genres",
                new UnwindOptions().preserveNullAndEmptyArrays(false)), group(and(eq("genre", "$genres"), eq("artist", "$artist")), sum("songs_count", 1L), avg("songs_avg_rating", "$avg_song_rating")), match(ne("songs_avg_rating",
                new BsonNull())), addFields(new Field("points",
                new Document("$sum", Arrays.asList(new Document("$multiply", Arrays.asList("$songs_count", 0.3d)),
                        new Document("$multiply", Arrays.asList("$songs_avg_rating", 0.7d)))))), sort(descending("points")),
                        group("$_id.genre", first("best_artist", "$_id.artist"))))
                .forEach(doc->{
                    String genre = doc.getString("_id.genre");
                    System.out.println(genre);
                    String artistUsername = doc.getString("best_artist");
                    System.out.println(artistUsername);
                    res.add(new Pair(genre, getArtistPreview(artistUsername)));
        });

        return res;
    }

    public int checkCredentials(String username, String password){
        Document user = usersCollection.aggregate(Arrays.asList(match(and(eq("username", username), eq("password", password))), addFields(new Field("isArtist",
                new Document("$cond", Arrays.asList(new Document("$ifNull", Arrays.asList("$stage_name", false)), true, false)))))).first();
        if(user == null){
            return -1;
        }
        else if (user.getBoolean("isArtist")){
            return 1;
        }
        else{
            return 0;
        }
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

    private ArtistPreview getArtistPreview(String username){
        Document artist = usersCollection.find(eq("username", username)).first();
        if(artist != null){
            return new ArtistPreview(username, artist.getString("stageName"), artist.getString("image"));
        }
        return null;
    }

    public Map<String, Object> getSongData(String SongID){

        Bson match = match((eq("_id", new ObjectId(SongID))));

        BasicDBList list = new BasicDBList();
        list.add("$reviews");
        list.add(15);
        Bson slice = new BasicDBObject("reviews", new BasicDBObject("$slice", list));
        Bson project = Aggregates.project(Projections.fields(include("_id", "title", "length", "links", "album", "reviews", "genres", "image"), slice));

        Bson limit = limit(1);

        Document song = songsCollection.aggregate(Arrays.asList(match, limit, project)).first();
        if(song != null){
            return getSongMap(song);
        }
        return null;
    }

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
        songValues.put("title", song.get("title"));
        songValues.put("id", song.getObjectId("_id").toString());
        songValues.put("duration", song.get("length"));
        songValues.put("image", song.get("image"));
        songValues.put("links", song.getList("sites", Document.class));
        songValues.put("album", song.get("album"));
        List<Map<String, Object>> reviews = new ArrayList<>();
        if(song.getList("reviews", Document.class) != null){
            for (Document reviewDoc: song.getList("reviews", Document.class)) {
                Map<String, Object> reviewMap = getReviewMap(reviewDoc, song.getObjectId("_id").toString());
                reviews.add(reviewMap);
            }
            songValues.put("reviews", reviews);
        }
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
        userValues.put("firstName", user.get("first_name"));
        userValues.put("lastName", user.get("last_name"));
        userValues.put("image", user.get("image"));
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
        artistValues.put("stageName", artist.get("stage_name"));
        artistValues.put("biography", artist.get("biography"));
        artistValues.put("activity", artist.get("activity"));
        artistValues.put("country", artist.get("country"));
        artistValues.put("firstName", artist.get("first_name"));
        artistValues.put("lastName", artist.get("last_name"));
        artistValues.put("image", artist.get("image"));
        artistValues.put("links", artist.get("sites"));
        return artistValues;
    }

    @Override
    public void close() {
        mongoClient.close();
    }
}
