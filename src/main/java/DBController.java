import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.time.Instant;
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

import javax.print.Doc;
import javax.print.DocFlavor;
import java.net.UnknownHostException;
import java.util.logging.Filter;

public class DBController {
    static int boundUserMongoID = -1; // Bound user, if no user bound => this field should be ""
    static ErrorCodes lastRCode = ErrorCodes.NO_ERROR;

    public static MongoClient mongoClient;
    public static MongoDatabase database;
    public static MongoCollection currentCollection;
    public static HashMap<Integer, String> RID2FunctionMap;

    // Convert String of type "yyyy-MM-dd HH:mm" to Int64 Epoch time
    static long IncapsulateDateToInt64(String time){
        //DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-ddTHH:mm::ss");
        LocalDateTime date = LocalDateTime.parse(time, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        return date.toEpochSecond(ZoneOffset.of("Z"));
    }

    // Convert Int64 Epoch time String of type "yyyy-MM-dd HH:mm"
    static String DecapsulteDateFromInt64(long epochTime){
        LocalDateTime date =
                LocalDateTime.ofInstant(Instant.ofEpochSecond(epochTime),
                        TimeZone.getTimeZone("Z").toZoneId());
        date.toString();
        return date.toString();
    }

    // This fuction accepts a document and parses all the values
    // to String, where each argument lays on separate line
    static String ParseValuesToString(Document doc){
        Object[] keys = doc.keySet().toArray();
        String result = "";
        for (int i = 0; i < keys.length; i++){
            result += doc.get(keys[i]).toString();
            result += '\n';
        }
        return result;
    }

    // dateA > dateB?
    static boolean DateCmp_Greater(String dateA, String dateB){
        long epochA = IncapsulateDateToInt64(dateA);
        long epochB = IncapsulateDateToInt64(dateB);
        if (epochA > epochB) return true;
        return false;
    }

    // dateA >= dateB?
    static boolean DateCmp_GreaterEqual(String dateA, String dateB){
        long epochA = IncapsulateDateToInt64(dateA);
        long epochB = IncapsulateDateToInt64(dateB);
        if (epochA >= epochB) return true;
        return false;
    }

    // dateA == dateB?
    static boolean DateCmp_Equals(String dateA, String dateB){
        long epochA = IncapsulateDateToInt64(dateA);
        long epochB = IncapsulateDateToInt64(dateB);
        if (epochA == epochB) return true;
        return false;
    }

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
                case 2:
                    respose = GetExpenseList(Args);
                    break;
                case 3:
                    respose = GetIncomeList(Args);
                    break;
                case 4:
                    respose = GetExpenseByID(Args);
                    break;
                case 8:
                    respose = PostIncome(Args);
                    break;
                default:
                    lastRCode = ErrorCodes.RID_NOT_FOUND;
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

    static boolean CheckIfLogged() throws Exception{
        if (boundUserMongoID == -1){
            lastRCode = ErrorCodes.NOT_LOGGED_IN;
            return false;
        }
        return true;
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
                lastRCode = ErrorCodes.LOGIN_FAILED_WRONG_CREDENTIALS;
                return "";
            }
            boundUserMongoID = Integer.parseInt(qresult.cursor().next().get("_id").toString());
            lastRCode = ErrorCodes.NO_ERROR;
            return "";
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

    // RID: 1
    static String GetSumOfExpenses(ArrayList<String> Args) throws Exception{
        if (!CheckIfLogged()) return "";
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
            result += data.get(keySet[i]).toString();
            result += '\n';
        }
        return result;
    }

    // RID: 7
    static String PostExpense(ArrayList<String> Args) throws Exception{
        if (!CheckIfLogged()) return "-1";
        SwitchToCollection("Expenses");
        if (Args.size() < 7){
            lastRCode = ErrorCodes.WRONG_ARGUMENT_COUNT;
            return "-1";
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
            newExpense.append("date", IncapsulateDateToInt64(Args.get(6)));
            currentCollection.insertOne(newExpense);
            return Integer.toString(pickedId);

        }
        catch (Exception excp){
            System.out.println(excp.getCause());
            lastRCode = ErrorCodes.GENERIC_ERROR;
            return "-1";
        }
    }

    // RID: 2
    static String GetExpenseList(ArrayList<String> Args) throws Exception{
        if (!CheckIfLogged()) return "";
        SwitchToCollection("Expenses");
        long dateLeft = IncapsulateDateToInt64(Args.get(0));
        long dateRight = IncapsulateDateToInt64(Args.get(1));
        String category = Args.get(2);
        FindIterable<Document> queryResult = currentCollection.find(
                Filters.and(
                        Filters.eq("user_id", boundUserMongoID),
                        Filters.eq("category", category),
                        Filters.gte("date", dateLeft),
                        Filters.lte("date", dateRight)
                )
        ).projection(Projections.exclude("user_id"));
        MongoCursor<Document> cursor = queryResult.cursor();

        String result = "";
        while (cursor.hasNext()){
            Document nextArg = new Document(cursor.tryNext());
            if (nextArg == null) break;
            result += ParseValuesToString(nextArg);
        }
        return result;
    }

    static String GetIncomeList(ArrayList<String> Args) throws Exception{
        if (!CheckIfLogged()) return "";
        SwitchToCollection("Incomes");
        long dateLeft = IncapsulateDateToInt64(Args.get(0));
        long dateRight = IncapsulateDateToInt64(Args.get(1));
        FindIterable<Document> queryResult = currentCollection.find(
                Filters.and(
                        Filters.eq("user_id", boundUserMongoID),
                        Filters.gte("date", dateLeft),
                        Filters.lte("date", dateRight)
                )
        ).projection(Projections.exclude("user_id"));
        MongoCursor<Document> cursor = queryResult.cursor();

        String result = "";
        while (cursor.hasNext()){
            Document nextArg = new Document(cursor.tryNext());
            if (nextArg == null) break;
            result += ParseValuesToString(nextArg);
        }
        return result;
    }

    // RID: 8
    static String PostIncome(ArrayList<String> Args) throws Exception{
        if (!CheckIfLogged()) return "";
        SwitchToCollection("Incomes");
        if (Args.size() < 6){
            lastRCode = ErrorCodes.WRONG_ARGUMENT_COUNT;
            return "-1";
        }
        try{
            Document newExpense = new Document();
            int pickedId = PickID();
            if (pickedId == -1){
                throw new Exception("No ID available");
            }
            newExpense.append("_id", pickedId);
            newExpense.append("user_id", boundUserMongoID);
            newExpense.append("title", Args.get(0));
            newExpense.append("name", Args.get(1));
            newExpense.append("price", Float.parseFloat(Args.get(2)));
            newExpense.append("quantity", Integer.parseInt(Args.get(3)));
            newExpense.append("description", Args.get(4));
            newExpense.append("date", IncapsulateDateToInt64(Args.get(5)));
            currentCollection.insertOne(newExpense);
            return Integer.toString(pickedId);
        }
        catch (Exception excp){
            System.out.println(excp.getCause());
            lastRCode = ErrorCodes.GENERIC_ERROR;
            return "-1";
        }
    }

    // RID: 4
    static String GetExpenseByID(ArrayList<String> Args) throws Exception{
        if (!CheckIfLogged()) return "";
        SwitchToCollection("Expenses");
        FindIterable<Document> queried = currentCollection.find(
                Filters.and(
                        Filters.eq("user_id", boundUserMongoID),
                        Filters.eq("_id", Integer.parseInt(Args.get(0)))
                )
        ).projection(Projections.exclude("user_id"));
        MongoCursor<Document> cursor = queried.cursor();
        if (cursor.hasNext()){
            String result = "";
            result += ParseValuesToString(cursor.next());
            return result;
        }
        lastRCode = ErrorCodes.ELEMENT_NOT_FOUND;
        return "";
    }
}
