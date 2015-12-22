package net.gangelov.transporter.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

public class HTTP {
    public static String get(String urlString) throws IOException {
        URL url = new URL(urlString);
        URLConnection connection = url.openConnection();

        BufferedReader reader = new BufferedReader(
            new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8"))
        );

        return reader.readLine();
    }
}
