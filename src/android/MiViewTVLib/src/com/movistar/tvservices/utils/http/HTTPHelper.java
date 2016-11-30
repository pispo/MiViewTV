package com.movistar.tvservices.utils.http;

import java.io.Reader;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

public class HTTPHelper {
    private static final String LOG_TAG = HTTPHelper.class.getSimpleName();

    public static Reader GET(String address, int port, String uri) throws IOException {
        Reader reader = null;

        URL url = new URL("http://" + address + ":" + port + uri);

        try {

            HttpURLConnection connection = safelyOpenConnection(url);

            int responseCode = safelyConnect(connection);

            switch (responseCode) {
                case HttpURLConnection.HTTP_OK:
                    reader = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8);

                default:
                    throw new IOException("Bad HTTP response: " + responseCode);
            }

        } finally {
            connection.disconnect();
        }

        return reader;
    }

    private static HttpURLConnection safelyOpenConnection(URL url) throws IOException {
        URLConnection conn;

        try {
            conn = url.openConnection();
        } catch (NullPointerException npe) {
            Log.w(LOG_TAG, "Bad URI? " + url);
            throw new IOException(npe);
        }

        if (!(conn instanceof HttpURLConnection)) {
            throw new IOException();
        }

        return (HttpURLConnection) conn;
    }

    private static int safelyConnect(HttpURLConnection connection) throws IOException {
        try {
            connection.connect();
        } catch (NullPointerException | IllegalArgumentException | IndexOutOfBoundsException | SecurityException e) {
            throw new IOException(e);
        }

        try {
            return connection.getResponseCode();
        } catch (NullPointerException | StringIndexOutOfBoundsException | IllegalArgumentException e) {
            throw new IOException(e);
        }
    }
}
