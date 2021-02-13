package it.unipi.iit.inginf.lsmdb.communitunes.persistence;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.client.*;
import com.mongodb.client.model.*;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.Artist;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.Link;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.Song;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.User;
import org.bson.*;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.javatuples.Triplet;

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Accumulators.*;

import static com.mongodb.client.model.Sorts.descending;
import static com.mongodb.client.model.Updates.*;

import java.io.Closeable;
import java.time.LocalDate;
import java.util.*;

class MongoDriver implements Closeable {
    private final MongoClient mongoClient;
    private final MongoCollection<Document> usersCollection;
    private final MongoCollection<Document> songsCollection;
    private final MongoCollection<Document> reportsCollection;

    MongoDriver(String connectionString){
        MongoDatabase database;
        if(connectionString != null){
            mongoClient = MongoClients.create(connectionString);
            database = mongoClient.getDatabase("project");
            usersCollection = database.getCollection("users");
            songsCollection = database.getCollection("songs");
            reportsCollection = database.getCollection("reports");
        }
        else{
            // TODO: raise exception
            mongoClient = null;
            usersCollection = null;
            songsCollection = null;
            reportsCollection = null;
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

    public boolean updateUser(String id, String password, String email, String country, LocalDate birthday, String firstName, String lastName, String image){
        Document query = new Document();
        query.append("_id", new ObjectId(id));
        BasicDBObject setData = new BasicDBObject();
        setData.append("password", password).append("email", email)
                .append("country", country).append("birthday", birthday)
                .append("first_name", firstName).append("last_name", lastName)
                .append("image", image);
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
        DeleteResult songsDelete = songsCollection.deleteMany(eq("artist", username));
        DeleteResult deleteResult = usersCollection.deleteOne(eq("username", username));
        return deleteResult.wasAcknowledged() && songsDelete.wasAcknowledged() && deleteResult.getDeletedCount() >= 1;
    }

    // TODO: check if a field is null. If it is, remove it from the document
    public boolean updateArtist(String id, String password, String email, String country, LocalDate birthday, String firstName, String lastName, String image, String activity, String biography, String stageName, List<Link> links){
        Document query = new Document();
        query.append("_id", new ObjectId(id));
        Document setData = new Document();
        BasicDBList linkList = new BasicDBList();
        for (Link link:
                links) {
            linkList.add(new BasicDBObject("name", link.name).append("link", link.url));
        }
        setData.append("password", password).append("email", email)
                .append("country", country).append("birthday", birthday)
                .append("activity", activity).append("image", image)
                .append("biography", biography).append("stageName", stageName)
                .append("first_name", firstName).append("last_name", lastName)
                .append("sites", linkList);
        Document update = new Document();
        update.append("$set", setData);

        UpdateResult updateRes = usersCollection.updateOne(query, update);

        return updateRes.getModifiedCount() != 0;
    }

    public String addSong(String artist, String duration, String title, String album, String image, List<Link> links, List<String> genres){
        Document song = new Document();
        BasicDBList linkList = new BasicDBList();
        if(links != null){
            for (Link link:
                    links) {
                linkList.add(new BasicDBObject("name", link.name).append("link", link.url));
            }
            song.append("links", linkList);
        }
        BasicDBList genreList = new BasicDBList();
        if(genres != null){
            genreList.addAll(genres);
            song.append("genres", genreList);
        }
        song.append("artist", artist);
        song.append("title", title);
        song.append("lenght", duration);
        song.append("album", album);
        song.append("image", image);

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
    public boolean updateSong(String id, String image, String album, List<String> genres, List<Link> links){
        Document query = new Document();
        query.append("_id", new ObjectId(id));
        Document setData = new Document();
        BasicDBList linkList = new BasicDBList();
        if(links != null){
            for (Link link:
                    links) {
                linkList.add(new BasicDBObject("name", link.name).append("link", link.url));
            }
        }
        setData.append("links", linkList)
                .append("image", image)
                .append("genres", genres)
                .append("album", album);
        Document update = new Document();
        update.append("$set", setData);

        UpdateResult updateRes = songsCollection.updateOne(query, update);

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

    public Map<String, List<Map<String, Object>>> getBestSongsForEachGenre(){
        HashMap<String, List<Map<String, Object>>> res = new HashMap<>();

        Document toProject = new Document("title", "$title")
                .append("avg_rating", "$avg_rating")
                .append("artist", "$artist")
                .append("_id", "$_id")
                .append("image", "$image");

        AggregateIterable<Document> result = songsCollection.aggregate(Arrays.asList(addFields(new Field("reviews",
                new Document("$filter",
                        new Document("input", "$reviews")
                                .append("as", "review")
                                .append("cond",
                                        new Document("$and", Arrays.asList(new Document("$gt", Arrays.asList(new java.util.Date(), "$$review.posted")),
                                                new Document("$lte", Arrays.asList(LocalDate.now().minusDays(30), "$$review.posted")))))))), addFields(new Field("avg_rating",
                new Document("$avg", "$reviews.rating"))), match(ne("avg_rating",
                new BsonNull())), sort(descending("avg_rating")), unwind("$genres",
                new UnwindOptions().preserveNullAndEmptyArrays(false)), group("$genres", Accumulators.push("songs", toProject)), addFields(new Field("songs",
                new Document("$slice", Arrays.asList("$songs", 6L)))))).allowDiskUse(true);
        for (Document resultDoc :
        result){
            String genre = resultDoc.getString("_id");
            List<Map<String, Object>> songMaps = new ArrayList<>();
            List<Document> songs = (ArrayList<Document>)resultDoc.get("songs");
            for (Document song : songs){
                songMaps.add(getEntityMap(song, Arrays.asList("title", "artist", "_id", "image")));
            }
            res.put(genre, songMaps);
        }
        return res;
    }

    public HashMap<String, String> getBestAndWorstAlbum(String username){
        HashMap<String, String> res = new HashMap<>();

        Document query = songsCollection.aggregate(Arrays.asList(match(eq("artist", username)), addFields(new Field("avg_song_rating",
                new Document("$avg", "$reviews.rating"))), match(ne("avg_song_rating",
                new BsonNull())), group("$album", avg("avg_album_rating", "$avg_song_rating")), sort(descending("avg_album_rating")), group("Album stats", first("best_album", "$_id"), last("worst_album", "$_id")))).first();

        res.put("best", query.getString("best_album"));
        res.put("worst", query.getString("worst_album"));

        return res;
    }

    public Map<String, String> getRepresentativeArtist(){
        Map<String, String> res = new HashMap<>();

        Bson group_id = new BasicDBObject("genre", "$genres")
                .append("artist", "$artist");

        songsCollection.aggregate(Arrays.asList(addFields(new Field("avg_song_rating",
                new Document("$avg", "$reviews.rating"))), unwind("$genres",
                new UnwindOptions().preserveNullAndEmptyArrays(false)), group(group_id, sum("songs_count", 1L), avg("songs_avg_rating", "$avg_song_rating")), match(ne("songs_avg_rating",
                new BsonNull())), addFields(new Field("points",
                new Document("$sum", Arrays.asList(new Document("$multiply", Arrays.asList("$songs_count", 0.3d)),
                        new Document("$multiply", Arrays.asList("$songs_avg_rating", 0.7d)))))), sort(descending("points")),
                        group("$_id.genre", first("best_artist", "$_id.artist"))))
                .forEach(doc->{
                    String genre = doc.getString("_id");
                    String artistUsername = doc.getString("best_artist");
                    res.put(genre, artistUsername);
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

    public boolean checkAdminCredentials(String username, String password){
        Bson myMatch = match(and(eq("username", username), eq("password", password)));
        Bson myProject = project(fields(include("isAdmin")));
        Document user = usersCollection.aggregate(Arrays.asList(myMatch, myProject)).first();

        if(user == null || user.getBoolean("isAdmin") == null){
            return false;
        }
        else {
            return true;
        }
    }

    public boolean deleteRequest(String username) {
        Bson filter = and(eq("username", username), exists("requestedStageName"));
        DeleteResult deleteResult = reportsCollection.deleteOne(filter);
        return deleteResult.wasAcknowledged() && deleteResult.getDeletedCount() >= 1;
    }

    public boolean deleteReport(String username) {
        Bson filter = and(eq("username", username), exists("numReports"));
        DeleteResult deleteResult = reportsCollection.deleteOne(filter);
        return deleteResult.wasAcknowledged() && deleteResult.getDeletedCount() >= 1;
    }

    public boolean deleteComment(String commentId){
        Bson filterReport = eq("comments._id", new ObjectId(commentId));
        Bson filterReview = eq("reviews._id", new ObjectId(commentId));

        DeleteResult deleteResultReport = reportsCollection.deleteOne(filterReport);
        DeleteResult deleteResultReview = reportsCollection.deleteOne(filterReview);

        return deleteResultReport.wasAcknowledged() && deleteResultReview.wasAcknowledged()
                && deleteResultReport.getDeletedCount() == 1 && deleteResultReview.getDeletedCount() == 1;
    }

    public List<Triplet<String, Integer, HashMap<String, String>>> getReports(){
        List<Triplet<String,Integer,HashMap<String, String>>> res = new ArrayList<>();

        Bson myCheck = exists("numReports");
        Bson mySort = sort(descending("numReports"));
        Bson myLimit = limit(50);

        reportsCollection.aggregate(Arrays.asList(myCheck, mySort, myLimit)).forEach(doc->{
            HashMap<String, String> comments = new HashMap<>();
            List<Document> commentsList = doc.getList("comments", Document.class);
            for(Document comment : commentsList){
                comments.put(comment.getString("_id"), comment.getString("review"));
            }

            res.add(new Triplet(doc.getString("username"), doc.getInteger("numReports"), comments));
        });

        return res;
    }

    public HashMap<String,String> getRequests(){
        HashMap<String,String> res = new HashMap<>();

        Bson myCheck = exists("requestedStageName");
        Bson myLimit = limit(50);

        reportsCollection.aggregate(Arrays.asList(myCheck, myLimit)).forEach(doc->{
            res.put(doc.getString("username"), doc.getString("requestedStageName"));
        });
        return res;
    }

    public Map<String, Object> getUserData(String username){
        Document user = usersCollection.find(eq("username", username)).first();
        if(user != null){
            return getUserMap(user);
        }
        return null;
    }

    public Map<String, Object> getArtistData(String username){
        Document artist = usersCollection.find(and(eq("username", username), ne("stage_name", null))).first();
        if(artist != null){
            return getArtistMap(artist);
        }
        return null;
    }

    public List<HashMap<String, Object>> getSongs(String title){
        List<HashMap<String, Object>> res = new ArrayList<>();

        Bson myCheck = regex("title", ".*" + title + ".*");

        BasicDBList list = new BasicDBList();
        list.add("$reviews");
        list.add(-15);
        Bson slice = new BasicDBObject("reviews", new BasicDBObject("$slice", list));
        Bson addField = Aggregates.addFields(new Field<>("avgRating", new BasicDBObject("$avg", "$reviews.rating")));
        Bson project = Aggregates.project(Projections.fields(include("_id", "title", "artist", "image"), slice));

        songsCollection.aggregate(Arrays.asList(myCheck, addField, project)).forEach(doc->{
            HashMap<String, Object> temp = new HashMap<>();
            temp.put("_id", doc.getObjectId("_id").toString());
            temp.put("title", doc.getString("title"));
            temp.put("artist", doc.getString("artist"));
            temp.put("image", doc.getString("image"));
            res.add(temp);
        });
        return res;
    }

    public List<HashMap<String, Object>> getReviewsByUsername(String username){
        List<HashMap<String, Object>> res = new ArrayList<>();

        Bson myMatch = eq("reviews.user", username);
        Bson myUnwind = unwind("$reviews");
        Bson myProject = fields(excludeId(), include("reviews._id", "reviews.user", "reviews.text", "_id"));

        songsCollection.aggregate(Arrays.asList(myMatch, myUnwind, myMatch, myProject)).forEach(doc->{
            HashMap<String, Object> temp = new HashMap<>();
            temp.put("id", doc.getObjectId("reviews._id").toString());
            temp.put("user", doc.getString("reviews.user"));
            temp.put("text", doc.getString("reviews.text"));
            temp.put("songID", doc.getObjectId("_id").toString());
            res.add(temp);
        });

        return res;
    }

    public Map<String, Object> getSongData(String SongID){

        Bson match = match((eq("_id", new ObjectId(SongID))));

        BasicDBList list = new BasicDBList();
        list.add("$reviews");
        list.add(-15);
        Bson slice = new BasicDBObject("reviews", new BasicDBObject("$slice", list));
        Bson addField = Aggregates.addFields(new Field<>("avgRating", new BasicDBObject("$avg", "$reviews.rating")));
        Bson project = Aggregates.project(Projections.fields(include("_id", "title", "length", "links", "album", "reviews", "genres", "image", "avgRating"), slice));
        Bson limit = limit(1);

        Document song = songsCollection.aggregate(Arrays.asList(match, limit, addField, project)).first();
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
                    .append("_id", reviewId)
                    .append("posted", current);

            if(text != null){
                reviewDoc.append("text", text);
            }

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
        list.add(nMax * -1);
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

    public List<Map<String, Object>> getSongReviews(String songID, int startIndex, int count){
        List<Map<String, Object>> reviews = new ArrayList<>();

        Bson match = match((eq("_id", new ObjectId(songID))));

        BasicDBList list = new BasicDBList();
        list.add("$reviews");
        list.add(startIndex);
        list.add(startIndex + count);

        Bson skip = skip(startIndex);
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
        songValues.put("links", song.getList("links", Document.class));
        songValues.put("album", song.get("album"));
        songValues.put("avgRating", song.get("avgRating"));
        List<Map<String, Object>> reviews = new ArrayList<>();
        List<Document> songList = song.getList("reviews", Document.class);
        if(songList != null){
            Collections.reverse(songList);
            for (Document reviewDoc: songList) {
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

    private Map<String, Object> getEntityMap(Document doc, List<String> fields){
        if(doc == null || fields == null){
            return null;
        }
        HashMap<String, Object> values = new HashMap<>();
        for (String field:
             fields) {
            if("_id".equals(field)){
                values.put(field, doc.get(field).toString());
            }
            else{
                values.put(field, doc.get(field));
            }
        }
        return values;
    }

    public boolean checkIfUserReviewedSong(String username, String songID){
        BasicDBObject criteria = new BasicDBObject();
        criteria.append("_id", new ObjectId(songID));
        criteria.append("reviews", new BasicDBObject("$elemMatch", new BasicDBObject("user", username)));
        return songsCollection.countDocuments(criteria) > 0;
    }

    public boolean checkIfStageNameExists(String stageName){
        return usersCollection.find(eq("stage_name", stageName)).first() != null;
    }

    public boolean checkIfRequestExists(String username){
        Bson myCheck = match(exists("requestedStageName"));
        Bson myMatch = match(eq("username", username));

        return reportsCollection.aggregate(Arrays.asList(myCheck, myMatch)).first() != null;
    }

    public boolean addRequest(String username, String stageName){
        Document user = new Document();
        user.append("username", username);
        user.append("requestedStageName", stageName);
        InsertOneResult insertOneResult = reportsCollection.insertOne(user);
        // TODO: probabilmente non serve ritornare l'ID
        return insertOneResult.getInsertedId() == null ? false : true;
    }

    @Override
    public void close() {
        mongoClient.close();
    }

    public List<Map<String, Object>> getArtistsWithFields(String identityField, List<String> identifiers, List<String> fields) {
        return getDataWithFields(usersCollection, identityField, identifiers, fields);
    }

    public List<Map<String, Object>> getUsersWithFields(String identityField, List<String> identifiers, List<String> fields) {
        return getDataWithFields(usersCollection, identityField, identifiers, fields);
    }

    public List<Map<String, Object>> getSongsWithFields(String identityField, List<String> identifiers, List<String> fields) {
        return getDataWithFields(songsCollection, identityField, identifiers, fields);
    }

    private List<Map<String, Object>> getDataWithFields(MongoCollection<Document> collection, String identityField, List<String> identifiers, List<String> fields){
        List<Map<String, Object>> resultList = new ArrayList<>();
        Bson match = match(in(identityField, identifiers));
        Bson project = Aggregates.project(fields(include(fields)));
        AggregateIterable<Document> result = collection.aggregate(Arrays.asList(match, project));
        for (Document doc:
                result) {
            Map<String, Object> entityMap = getEntityMap(doc, fields);
            resultList.add(entityMap);
        }
        return resultList;
    }
}
