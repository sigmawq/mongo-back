

import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.net.UnknownHostException;
import java.util.ArrayList;

public class MongoDB {
    public static void main(String[] args) throws UnknownHostException {
        /*MongoClientURI cloudDB_admin = new MongoClientURI("mongodb+srv://main_user:111@cluster0.onas9.gcp.mongodb.net/test?retryWrites=true&w=majority");
        MongoClientURI cloudDB_useraccess = new MongoClientURI("mongodb+srv://user_access:111@cluster0.onas9.gcp.mongodb.net/test?retryWrites=true&w=majority");
        MongoClientURI localDB = new MongoClientURI("mongodb://localhost:27017");*/
        DBController.ConnectToMongoDB("mongodb+srv://main_user:111@cluster0.onas9.gcp.mongodb.net/MongoDBTutorial?retryWrites=true&w=majority");
        ArrayList<String> credentials = new ArrayList<>();
        credentials.add("TestUser");
        credentials.add("V12");
        DBController.SwitchToDB("MongoDBTutorial");
        DBController.SwitchToCollection("Users");
        DBController.PerformLogin(credentials);
    }
}
