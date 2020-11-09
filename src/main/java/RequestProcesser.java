import java.io.*;
import java.util.ArrayList;

public class RequestProcesser {
    static private void WriteLine(BufferedWriter writer, String sequence){
        try{
            writer.write(sequence);
            writer.newLine();
        }
        catch (Exception excp){
            System.out.println(excp.getCause());
        }
    }
    // Send request using file .rin
    // Returns path to .rout (typically in the same folder)
    public static void SendRequest_F(String filePath, String outputPath) throws Exception{
        ArrayList<String> Args = new ArrayList<>();
        File outFile = new File(outputPath + "rout");
        BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
        BufferedReader reader;
        try{
            reader = new BufferedReader(new FileReader(filePath));
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
            String result = DBController.ProcessRequest(Args);


            // Write RCode
            WriteLine(writer, Integer.toString(DBController.lastRCode.ordinal()));

            // Write return set of values
            WriteLine(writer, result);
            writer.close();
        }
        catch (Exception excp){
            // Failed to open request file
            // Write RCode
            WriteLine(writer, Integer.toString(ErrorCodes.IO_FAILTURE.ordinal()));
            writer.close();
        }
    }

    // Send request using String
    // Returns path to .rout (typically in the same folder)
    public static String SendRequest_S(String requestString){
        // TODO
        return "";
    }
}
