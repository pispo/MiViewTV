package com.MovistarPlusService;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

/**
 * Created by Sergio on 31/01/2016.
 */
public class MulticastDataSource {

    private static final int MAX_DGRAM_SIZE = 5000;
    public static final int TIMEOUT = 1000;

    private InetAddress serviceAddress;
    private int servicePort;
    private MulticastSocket serviceSocket;
    byte[] buf = new byte[MAX_DGRAM_SIZE];
    DatagramPacket packet = new DatagramPacket(buf, buf.length);

    public MulticastDataSource (String address, int port) throws UnknownHostException, IllegalArgumentException {
        String errorString = "";

        this.serviceAddress = InetAddress.getByName(address);
        this.servicePort = port;

        if (!serviceAddress.isMulticastAddress()) {
            errorString = "Address (" + address + ") is not a multicast network address";
        }
        if (!(servicePort > 1024 && servicePort < 65536)) {
            if (errorString != "") {
                errorString += " and ";
            }
            errorString += " Port (" + port + ") is out of range [1024-65536]";
        }

        if (errorString != "") {
            errorString += ".";
            throw new IllegalArgumentException(errorString);
        }
    }

    public void attach() throws IOException{
        try {
            serviceSocket = new MulticastSocket(new InetSocketAddress(serviceAddress, servicePort));
            serviceSocket.setSoTimeout(TIMEOUT);
            serviceSocket.joinGroup(serviceAddress);
        } catch (IOException e) {
            e.printStackTrace();
            detach();
            throw e;
        }
    }

    public void detach() {
        try {
            serviceSocket.leaveGroup(this.serviceAddress);
            serviceSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        serviceSocket = null;
    }

    public DatagramPacket readPacket() {
        if (null != serviceSocket) {
            try {
                serviceSocket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            return packet;
        }
        return null;
    }

}
