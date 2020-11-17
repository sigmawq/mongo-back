package mp;

import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import mp.*;

public class MongoDB {
    public static void main(String[] args) throws UnknownHostException {
        /*MongoClientURI cloudDB_admin = new MongoClientURI("mongodb+srv://main_user:111@cluster0.onas9.gcp.mongodb.net/test?retryWrites=true&w=majority");
        MongoClientURI cloudDB_useraccess = new MongoClientURI("mongodb+srv://user_access:111@cluster0.onas9.gcp.mongodb.net/test?retryWrites=true&w=majority");
        MongoClientURI localDB = new MongoClientURI("mongodb://localhost:27017");*/
        DBController.ConnectToMongoDB("mongodb+srv://main_user:111@cluster0.onas9.gcp.mongodb.net/MongoDBTutorial?retryWrites=true&w=majority");
        ArrayList<String> credentials = new ArrayList<>();
        try {
            System.out.println("i: " + args[0] + ' ' + "o: " + args[1]);
            RequestProcesser.SendRequest_F(args[0], args[1]);
        } catch (Exception excp) {

        }

        /*credentials.add("TestUser");
        credentials.add("V12");
        DBController.SwitchToDB("MongoDBTutorial");
        DBController.SwitchToCollection("Users");
        try{
            RequestProcesser.SendRequest_F("/home/sigmawq/IdeaProjects/mongo-back/mongo-back/login.rin",
                    "/home/sigmawq/IdeaProjects/mongo-back/mongo-back/");
            RequestProcesser.SendRequest_F("/home/sigmawq/IdeaProjects/mongo-back/mongo-back/example_GetExpenseByID.txt",
                    "/home/sigmawq/IdeaProjects/mongo-back/mongo-back/");
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

        ArrayList<String> args_3 = new ArrayList<>();
        args_3.add("Food");
        args_3.add("Title");
        args_3.add("Meat");
        args_3.add("5.5");
        args_3.add("1");
        args_3.add("None");
        args_3.add("2020-10-12T12:10:00");
        //DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        //LocalDateTime date = LocalDateTime.parse("2020-10-10 23:00", format);

        //System.out.println("Seconds: " + date.toEpochSecond(ZoneOffset.of("Z")));
        //System.out.println(date.toString());

        try{
            //mp.DBController.PostExpense(args_3);
            long incapsulated = DBController.IncapsulateDateToInt64("2020-10-10T23:00:00");
            String decapsulated = DBController.DecapsulteDateFromInt64(incapsulated);
            System.out.println("1");

        }
        catch(Exception excp){
            System.out.println(excp.getCause().toString());
        }
    }

         */
    }
}