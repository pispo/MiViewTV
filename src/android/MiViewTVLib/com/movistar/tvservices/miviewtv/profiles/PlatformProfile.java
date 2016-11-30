package com.movistar.tvservices.miviewtv.profiles;

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

    public static PlatformProfile fromJSONStream(Reader reader)
    {
        PlatformProfile platformProfile = new PlatformProfile();
        JsonReader jsonReader;

        try {

            jsonReader = new JsonReader(reader);
            jsonReader.beginObject();

            while (jsonReader.hasNext()) {

                if (jsonReader.nextName().equals("resultData")) {

                    jsonReader.beginObject();

                    while (jsonReader.hasNext()) {

                        if (jsonReader.nextName().equals("dvbConfig")) {

                            jsonReader.beginObject();

                            while (jsonReader.hasNext()) {
                                String name = jsonReader.nextName();

                                if (name.equals("dvbServiceProvider")) {
                                    platformProfile.setDvbServiceProvider(jsonReader.nextString());
                                } else if (name.equals("dvbEntryPoint")) {
                                    platformProfile.setDvbEntryPoint(jsonReader.nextString());
                                } else if (name.equals("updateTime")) {
                                    platformProfile.setUpdateTime(jsonReader.nextInt());
                                } else {
                                    jsonReader.skipValue();
                                }
                            }

                            jsonReader.endObject();
                        }
                    }

                    jsonReader.endObject();

                } else {
                    jsonReader.skipValue();
                }
            }

            jsonReader.endObject();

        } catch (IOException e) {
            return null;

        } finally {
            try {
                jsonReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}