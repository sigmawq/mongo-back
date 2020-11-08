import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class RequestProcesser {
    // Send request using file .rin
    // Returns path to .rout (typically in the same folder)
    public static String SendRequest_F(String filePath) throws Exception{
        ArrayList<String> Args = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));

        // Get RID
        String rid = reader.readLine();
        Args.add(rid);

        // Get all other args
        boolean go = true;
        while (go){
            String newArg = reader.readLine();
            if (newArg == null) break;
            Args.add(newArg);
        }
        File outFile = new File("rout");
        return DBController.ProcessRequest(Args);

    }

    // Send request using String
    // Returns path to .rout (typically in the same folder)

    public static String SendRequest_S(String requestString){
        // TODO
        return "";
    }
}
