import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.*;
import org.bson.Document;

import java.net.UnknownHostException;

public class DBController {
    static ErrorCodes returnCode; // Return code here
    static String lastError; // Error description goes here
    static String boundUserMongoID; // Bound user, if no user bound => this field should be ""
    static String returnObject; // All request data should be returned here

    public static MongoClient mongoClient;
    public static MongoDatabase database;
    public static MongoCollection currentCollection;

    static void ConnectToMongoDB(String URI){
        MongoClientURI mainClient = new MongoClientURI(URI);
        mongoClient = new MongoClient(mainClient);
    }

    static void SwitchToDB(String DBname){
        database = mongoClient.getDatabase(DBname);
    }

    static void SwitchToCollection(String CollectionName){
        currentCollection = currentCollection = database.getCollection("test");
    }

    static Cursor SearchFor_In(Document queryDocument){
        FindIterable queryResult = currentCollection.find(queryDocument);
    }

    static RListForm_Input ProcessInput(String path) throws Exception{
        try{
            RListForm_Input result = new RListForm_Input();
            BufferedReader inputFile = new BufferedReader(new FileReader(path));
            String RID = inputFile.readLine();
            String[] argList;
            String currentArg = new String();
            while (currentArg != null){
                currentArg = inputFile.readLine();
                result.args.add(currentArg);
            }
            return result;
        }
        catch (Exception excp){
            System.out.println("bla bla");
            throw new Exception();
        }
    }
    static RListForm_Output ProcessOutput(String data) {
        return new RListForm_Output();
    }

    // RID 0:
    static boolean PerformLogin(ArrayList<String> Args) {
        if (boundUserMongoID != ""){
            lastError = "Attempted to log in while already logged in";
            returnCode = ErrorCodes.GENERIC_ERROR;
            return false;
        }
        if (Args.size() < 2){
            lastError = "Provided argument count is less than expected";
            returnCode = ErrorCodes.WRONG_ARGUMENT_AMOUNT;
            return false;
        }
        BasicDBObject userToFind = new BasicDBObject();
        userToFind.append("Username", Args.get(0));
        userToFind.append("Password", Args.get(1));
        FindIterable<Document> qresult = currentCollection.find(Filters.and(Filters.eq("Username", Args.get(0)),
                                                                Filters.eq("Password", Args.get(1))));
        if (!qresult.cursor().hasNext()){
            lastError = "User not found";
            returnCode = ErrorCodes.LOGIN_FAILED;
            return false;
        }
        boundUserMongoID = qresult.cursor().next().get("ObjectId").toString();
        return true;
    }

    // RID: 9
    static void PerformLogOut(){
        boundUserMongoID = "";
    }

    // RID: 2
    static int GetExpenceList(ArrayList<String> Args){
        if (Args.size() < 3) return 1;
    }
}
