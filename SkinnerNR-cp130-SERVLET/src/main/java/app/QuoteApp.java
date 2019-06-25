package app;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

public class QuoteApp {
    private static void exec(final String symbol, final String format) {
        HttpURLConnection conn = null;
        String baseUrl = "http://localhost:8080/StockQuote/StockQuoteServlet";
        try {
            String urlStr = String.format("%s?symbol=%s&format=%s", baseUrl, symbol, format);
            System.out.println(urlStr);
            URL url = new URL(urlStr);
            conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");            
            
            System.out.printf("Content-Type: %s%n", conn.getContentType());
            InputStream in = conn.getInputStream();
            Reader rdr = new InputStreamReader(in);
            char buf[] = new char[1024];
            int len = 0;
            while ((len = rdr.read(buf)) != -1) {
                System.out.print(new String(buf, 0, len));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) {
        String courtesyType = "goog";
        
        System.out.println("================================================================================");
        System.out.println("JSON:");
        System.out.println("--------------------------------------------------------------------------------");
        exec(courtesyType, "json");
        System.out.println();
        System.out.println("================================================================================");
        System.out.println();
        System.out.println("================================================================================");
        System.out.println("plain:");
        System.out.println("--------------------------------------------------------------------------------");
        exec(courtesyType, "plain");
        System.out.println();
        System.out.println("================================================================================");
        System.out.println();
        System.out.println("================================================================================");
        System.out.println("HTML:");
        System.out.println("--------------------------------------------------------------------------------");
        exec(courtesyType, "html");
        System.out.println();
        System.out.println("================================================================================");
        System.out.println();
        System.out.println("================================================================================");
        System.out.println("XML:");
        System.out.println("--------------------------------------------------------------------------------");
        exec(courtesyType, "xml");
        System.out.println();
        System.out.println("================================================================================");
        
        System.out.println();
        System.out.println();
    }
}
