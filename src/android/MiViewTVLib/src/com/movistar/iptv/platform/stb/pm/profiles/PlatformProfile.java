package com.movistar.iptv.platform.stb.pm.profiles;

import android.util.Log;
import android.util.JsonReader;

import java.io.Reader;
import java.io.IOException;

public class PlatformProfile {
    private static final String LOG_TAG = PlatformProfile.class.getSimpleName();

    private String dvbEntryPoint;
    private String dvbServiceProvider;

    private int updateTime;

    public String getDvbEntryPoint() { return dvbEntryPoint; }

    public String getDvbServiceProvider() { return dvbServiceProvider; }

    public int getUpdateTime() { return updateTime; }

    public void setDvbEntryPoint(String dvbEntryPoint) { this.dvbEntryPoint = dvbEntryPoint; }

    public void setDvbServiceProvider(String dvbServiceProvider) { this.dvbServiceProvider = dvbServiceProvider; }

    public void setUpdateTime(int updateTime) { this.updateTime = updateTime; }

    public static PlatformProfile fromJSONStream(Reader reader) throws ProfileException {

        String name = null;
        JsonReader jsonReader = null;
        PlatformProfile platformProfile = new PlatformProfile();

        try {

            jsonReader = new JsonReader(reader);

            jsonReader.beginObject();

            while (jsonReader.hasNext()) {

                if (jsonReader.nextName().equals("resultData")) {

                    jsonReader.beginObject();

                    while (jsonReader.hasNext()) {

                        name = jsonReader.nextName();

                        if (name.equals("dvbConfig")) {

                            jsonReader.beginObject();

                            while (jsonReader.hasNext()) {
                                name = jsonReader.nextName();

                                if (name.equals("dvbServiceProvider")) {
                                    platformProfile.setDvbServiceProvider(jsonReader.nextString());
                                } else if (name.equals("dvbEntryPoint")) {
                                    platformProfile.setDvbEntryPoint(jsonReader.nextString());
                                } else {
                                    jsonReader.skipValue();
                                }
                            }

                            jsonReader.endObject();

                        } else if (name.equals("updateTime")) {
                            platformProfile.setUpdateTime(jsonReader.nextInt());

                        } else {
                            jsonReader.skipValue();
                        }
                    }

                    jsonReader.endObject();

                } else {
                    jsonReader.skipValue();
                }
            }

            jsonReader.endObject();

        } catch (IOException e) {
            e.printStackTrace();
            throw new ProfileException("Error: Failed to download the platform profile", e);

        } finally {
            try {
                if (jsonReader != null)
                    jsonReader.close();

                reader.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return platformProfile;
    }
}
