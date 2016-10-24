package com.movistar.miviewtv.core.net;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by sergio on 15/12/15.
 */
public class HttpDataSource {

    private InputStream in = null;
    private HttpURLConnection urlConnection = null;
    private URL url = null;

    /**
     * Constructor
     */
    public HttpDataSource(String url) throws IOException, MalformedURLException {
        this.url = new URL(url);
        try {
            attach();
        } catch (IOException e) {
            e.printStackTrace();
            detach();
            throw e;
        }
    }

    /**
     * Methods
     *
     */
    public void attach() throws IOException {
        if (null == this.url) {
            throw new MalformedURLException();
        }
        this.urlConnection = (HttpURLConnection) this.url.openConnection();
        try {
            this.in = new BufferedInputStream(this.urlConnection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            detach();
            throw e;
        }
    }

    public void detach() {
        in = null;
        if (null != urlConnection) {
            urlConnection.disconnect();
            urlConnection = null;
        }
    }

    public InputStream getInputStream () {
        return in;
    }
}
