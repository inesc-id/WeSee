package example.ws.cli.input;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class CheckRunArgs {
    public static String getUrl(String[] args)
    {
        if (args.length < 1)
            printErrorMessage();

        try
        {
            new URL(args[0]);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            printErrorMessage();
        }
        return args[0];
    }

    private static void printErrorMessage()
    {
        System.out.print("put server url");
        System.exit(1);
    }
}
