package com.movistar.tvservices.utils.http;

import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;

import java.lang.NullPointerException;
import java.lang.IllegalArgumentException;
import java.lang.SecurityException;
import java.lang.IndexOutOfBoundsException;
import java.lang.StringIndexOutOfBoundsException;

import java.io.Reader;
import java.io.IOException;

public class HTTPHelper {
    private static final String LOG_TAG = HTTPHelper.class.getSimpleName();

    public static Reader GET(String address, int port, String uri) throws IOException {
        Reader reader = null;
        HttpURLConnection connection = null;

        URL url = new URL("http://" + address + ":" + port + uri);

        try {

            connection = safelyOpenConnection(url);
            int responseCode = safelyConnect(connection);

            switch (responseCode) {
                case HttpURLConnection.HTTP_OK:
                    reader = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8);

                default:
                    throw new IOException("Bad HTTP response: " + responseCode);
            }

        } finally {
            if (connection != null)
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
        } catch (NullPointerException e) {
            throw new IOException(e);
        } catch (IllegalArgumentException e) {
            throw new IOException(e);
        } catch (IndexOutOfBoundsException e) {
            throw new IOException(e);
        } catch (SecurityException e) {
            throw new IOException(e);
        }

        try {
            return connection.getResponseCode();
        } catch (NullPointerException e) {
            throw new IOException(e);
        } catch (StringIndexOutOfBoundsException e) {
            throw new IOException(e);
        } catch (IllegalArgumentException e) {
            throw new IOException(e);
        }
    }
}
