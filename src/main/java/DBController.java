import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.*;
import org.bson.Document;

import java.net.UnknownHostException;

public class DBController {
    static int boundUserMongoID = -1; // Bound user, if no user bound => this field should be ""

    public static MongoClient mongoClient;
    public static MongoDatabase database;
    public static MongoCollection currentCollection;
    public static HashMap<Integer, String> RID2FunctionMap;

    static String ProcessRequest(ArrayList<String> Args){
        String respose = "";
        try{
            int RID = Integer.parseInt(Args.get(0));
            Args.remove(0);
            switch (RID){
                case 0:
                    respose = PerformLogin(Args);
                    break;
                case 1:
                    respose = GetSumOfExpenses(Args);
                    break;
                default:
                    throw new Exception("RID not found");
            }
            return respose;
        }
        catch (Exception excp){
            System.out.println(excp.getCause());
        }
        return respose;
    }

    static void ConnectToMongoDB(String URI){
        MongoClientURI mainClient = new MongoClientURI(URI);
        mongoClient = new MongoClient(mainClient);
    }

    static void SwitchToDB(String DBname){
        database = mongoClient.getDatabase(DBname);
    }

    static void SwitchToCollection(String CollectionName){
        currentCollection = currentCollection = database.getCollection(CollectionName);
    }

    static void CheckIfLogged() throws Exception{
        if (boundUserMongoID == -1){
            throw new Exception("Not logged in");
        }
    }

    // Check ian object with value "val" of key "key" exists in currentCollection
    static boolean CheckIfExists(String key, String val){
        Document query = new Document();
        query.append(key, val);
        FindIterable<Document> res = currentCollection.find(query);
        if (res.cursor().hasNext()) return true;
        return false;
    }

    static boolean CheckIfExists(String key, int val){
        Document query = new Document();
        query.append(key, val);
        FindIterable<Document> res = currentCollection.find(query);
        if (res.cursor().hasNext()) return true;
        return false;
    }

    // Pick unique id for current collection.
    static int PickID(){
        for (int i = 0; i < 2147483647; i++){
            if (!(CheckIfExists("_id", i))){
                return i;
            }
        }
        return -1;
    }

    // RID 0:
    static String PerformLogin(ArrayList<String> Args) throws Exception{
        SwitchToCollection("Users");
        try{
            if (boundUserMongoID != -1){
                throw new Exception("Attempted to log in while already logged in");
            }
            if (Args.size() < 2){
                throw new Exception("Provided element count is less than expected");
            }
            BasicDBObject userToFind = new BasicDBObject();
            userToFind.append("username", Args.get(0));
            userToFind.append("password", Args.get(1));
            FindIterable<Document> qresult = currentCollection.find(userToFind);
            if (!(qresult.cursor().hasNext())){
                return "0";
            }
            boundUserMongoID = Integer.parseInt(qresult.cursor().next().get("_id").toString());
            return "1";
        }
        catch (Exception excp){
            System.out.println(excp.getCause());
        }
        return "";
    }

    // RID: 9
    static void PerformLogOut(){
        boundUserMongoID = -1;
    }

    // RID: 2
    static String GetSumOfExpenses(ArrayList<String> Args) throws Exception{
        CheckIfLogged();
        SwitchToCollection("Expenses");
        HashMap<String, Float> data = new HashMap<>();
        Document query = new Document();
        query.append("user_id", boundUserMongoID);
        FindIterable<Document> queryResult = currentCollection.find(query);
        MongoCursor<Document> cursor = queryResult.cursor();
        while (queryResult.cursor().hasNext()){
            Document next = cursor.tryNext();
            if (next == null) break;
            String category = next.get("category").toString();
            float price = Float.parseFloat(next.get("price").toString());
            int amount = Integer.parseInt(next.get("quantity").toString());
            if (data.containsKey(category)){
                data.put(category, new Float(data.get(category).floatValue()
                        + price * amount));
            }
            else{
                data.put(category, new Float(price * amount));
            }
        }
        String result = new String();
        Object[] keySet = data.keySet().toArray();
        for (int i = 0; i < data.size(); i++){
            result += keySet[i];
            result += '\n';
            result += data.get(i).toString();
            result += '\n';
        }

        return result;
    }

    // RID: 7
    static int PostExpense(ArrayList<String> Args) throws Exception{
        CheckIfLogged();
        SwitchToCollection("Expenses");
        if (Args.size() < 7){
            throw new Exception("Wrong argument count");
        }
        try{
            Document newExpense = new Document();
            int pickedId = PickID();
            if (pickedId == -1){
                throw new Exception("No ID available");
            }
            newExpense.append("_id", pickedId);
            newExpense.append("user_id", boundUserMongoID);
            newExpense.append("category", Args.get(0));
            newExpense.append("title", Args.get(1));
            newExpense.append("name", Args.get(2));
            newExpense.append("price", Float.parseFloat(Args.get(3)));
            newExpense.append("quantity", Integer.parseInt(Args.get(4)));
            newExpense.append("description", Args.get(5));
            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime date = LocalDateTime.parse("2020-10-10 23:00", format);
            newExpense.append("date", date.toEpochSecond(ZoneOffset.of("Z")));
            currentCollection.insertOne(newExpense);
            return pickedId;

        }
        catch (Exception excp){
            System.out.println(excp.getCause());
            return -1;
        }
    }
    // RID: 8
    static int PostIncome(){
        return -1;
    }
}
