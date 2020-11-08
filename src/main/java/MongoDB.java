
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
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
        try{
            RequestProcesser.SendRequest_F("generic_request.rin");
        }
        catch(Exception e){
        }
        System.out.println("User: " + DBController.boundUserMongoID);

        ArrayList<String> args_2 = new ArrayList<>();
        args_2.add("Electronics");
        args_2.add("Title");
        args_2.add("Smartphone");
        args_2.add("2000");
        args_2.add("1");
        args_2.add("None");
        args_2.add("09/10/2020 12:10");
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime date = LocalDateTime.parse("2020-10-10 23:00", format);
        System.out.println("Seconds: " + date.toEpochSecond(ZoneOffset.of("Z")));
        System.out.println(date.toString());

        try{
            //DBController.PostExpense(args_2);

        }
        catch(Exception excp){
            System.out.println(excp.getCause().toString());
        }

    }
}
