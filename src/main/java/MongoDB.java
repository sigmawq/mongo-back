

import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.net.UnknownHostException;

public class MongoDB {
    public static MongoClient mongoClient;
    public static MongoDatabase database;
    public static MongoCollection currentCollection;

    static private void AccessUserCollection(){
        database = mongoClient.getDatabase("MongoDBTutorial");
        currentCollection = database.getCollection("Users");
    }
    public static void main(String[] args) throws UnknownHostException {
        MongoClientURI cloudDB_admin = new MongoClientURI("mongodb+srv://main_user:111@cluster0.onas9.gcp.mongodb.net/test?retryWrites=true&w=majority");
        MongoClientURI cloudDB_useraccess = new MongoClientURI("mongodb+srv://user_access:111@cluster0.onas9.gcp.mongodb.net/test?retryWrites=true&w=majority");
        MongoClientURI localDB = new MongoClientURI("mongodb://localhost:27017");
        mongoClient = new MongoClient(localDB);
        database = mongoClient.getDatabase("MongoDBTutorial");
        currentCollection = database.getCollection("test");
        //DBObject obj = new BasicDBObject("empolyee", "bob").append("items", new int[]).append("table", "table5");


        AddUser("Johny", "1488Guy");
        User queriedUser = GetUser("Johny", "1488Guy");
        System.out.println(queriedUser);
    }
    public static DBObject Convert(TestObject testObj){
        return new BasicDBObject("XP", testObj.xp).append("Timer", testObj.timer).append("memeberID", testObj.memberID);
    }
    public static void AddUser(String name, String nickname){
        AccessUserCollection();
        Document usr = new User(name, nickname).Convert();
        currentCollection.insertOne(usr);
    }
    public static User GetUser(String name, String nickname){
        AccessUserCollection();
        Document query = new Document("Name", name).append("Nickname", nickname);
        FindIterable<Document> iterable = currentCollection.find(query);
        User queriedUser = new User("-", "-");
        if (iterable.iterator().hasNext()){

            iterable.
            queriedUser = new User( temptoString(), temp.toString());
        }
        else{
            System.out.println("Failed to query user");
        }
        return queriedUser;
    }
}
