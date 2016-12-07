package com.movistar.tvservices.miviewtv.profiles;

import android.util.JsonReader;

import java.io.Reader;
import java.io.IOException;

public class ClientProfile {
    private static final String LOG_TAG = ClientProfile.class.getSimpleName();

    private int clientId;
    private int demarcation;
    private int clientSegment;
    private int partition;
    private int tvWholesaler;
    private int clientVersion;

    private String tvPackages;
    private String adminCode;
    private String publicIP;

    private int numIPs;
    private int hdTV;
    private int updateTime;

    public int getClientId() { return clientId; }

    public int getDemarcation() { return demarcation; }

    public int getClientSegment() { return clientSegment; }

    public int getPartition() { return partition; }

    public int getTvWholesaler() { return tvWholesaler; }

    public int getClientVersion() { return clientVersion; }

    public int getHdTV() { return hdTV; }

    public int getNumIPs() { return numIPs; }

    public int getUpdateTime() { return updateTime; }

    public String getAdminCode() { return adminCode; }

    public String getTvPackages() { return tvPackages; }


    public void setClientId(int clientId) { this.clientId = clientId; }

    public void setDemarcation(int demarcation) { this.demarcation = demarcation; }

    public void setClientSegment(int clientSegment) { this.clientSegment = clientSegment; }

    public void setPartition(int partition) { this.partition = partition; }

    public void setTvWholesaler(int tvWholesaler) { this.tvWholesaler = tvWholesaler; }

    public void setClientVersion(int clientVersion) { this.clientVersion = clientVersion; }

    public void setHdTV(int hdTV) { this.hdTV = hdTV; }

    public void setNumIPs(int numIPs) { this.numIPs = numIPs; }

    public void setUpdateTime(int updateTime) { this.updateTime = updateTime; }

    public void setAdminCode(String adminCode) { this.adminCode = adminCode; }

    public void setTvPackages(String tvPackages) { this.tvPackages = tvPackages; }

    public void setPublicIP(String publicIP) { this.publicIP = publicIP; }


    public static ClientProfile fromJSONStream(Reader reader)
    {
        JsonReader jsonReader = null;
        ClientProfile clientProfile = new ClientProfile();        

        try {

            jsonReader = new JsonReader(reader);
            jsonReader.beginObject();

            while (jsonReader.hasNext()) {

                if (jsonReader.nextName().equals("resultData")) {

                    jsonReader.beginObject();

                    while (jsonReader.hasNext()) {
                        String name = jsonReader.nextName();

                        if (name.equals("clientId")) {
                            clientProfile.setClientId(jsonReader.nextInt());
                        } else if (name.equals("demarcation")) {
                            clientProfile.setDemarcation(jsonReader.nextInt());
                        } else if (name.equals("clientSegment")) {
                            clientProfile.setClientSegment(jsonReader.nextInt());
                        } else if (name.equals("partition")) {
                            clientProfile.setPartition(jsonReader.nextInt());
                        } else if (name.equals("tvWholesaler")) {
                            clientProfile.setTvWholesaler(jsonReader.nextInt());
                        } else if (name.equals("clientVersion")) {
                            clientProfile.setClientVersion(jsonReader.nextInt());
                        } else if (name.equals("tvPackages")) {
                            clientProfile.setTvPackages(jsonReader.nextString());
                        } else if (name.equals("adminCode")) {
                            clientProfile.setAdminCode(jsonReader.nextString());
                        } else if (name.equals("publicIP")) {
                            clientProfile.setPublicIP(jsonReader.nextString());
                        } else if (name.equals("hdtv")) {
                            clientProfile.setHdTV(jsonReader.nextInt());
                        } else if (name.equals("numIPs")) {
                            clientProfile.setNumIPs(jsonReader.nextInt());
                        } else if (name.equals("updateTime")) {
                            clientProfile.setUpdateTime(jsonReader.nextInt());
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
            return null;

        } finally {
            try {
                if (jsonReader != null)
                    jsonReader.close();
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return clientProfile;
    }
}
