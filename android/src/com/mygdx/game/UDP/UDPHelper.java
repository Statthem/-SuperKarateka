package com.mygdx.game.UDP;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDPHelper extends Thread {
    private BroadcastListener listener;
    private Context ctx;
    private DatagramSocket socket;
    private static final int PORT = 5050;

    public UDPHelper(Context ctx, BroadcastListener listener) throws IOException {
        this.listener = listener;
        this.ctx = ctx;
    }

    public void send(String msg) throws IOException {
        DatagramSocket clientSocket = new DatagramSocket();
        clientSocket.setBroadcast(true);
        byte[] sendData = msg.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(
                sendData, sendData.length, getBroadcastAddress(), PORT);
        clientSocket.send(sendPacket);
    }

    @Override
    public void run() {
        try {
            socket = new DatagramSocket(PORT);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        while (!socket.isClosed()) {
            try {
                byte[] buf = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                listener.onReceive(
                        new String(packet.getData(), 0, packet.getLength()),
                        packet.getAddress().getHostAddress());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        socket.close();
    }

    public void end() {
        socket.close();
    }

    public interface BroadcastListener {
        public void onReceive(String msg, String ip);
    }


    InetAddress getBroadcastAddress() throws IOException {
        WifiManager wifi = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = wifi.getDhcpInfo();
        if(dhcp == null)
            return InetAddress.getByName("255.255.255.255");
        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        return InetAddress.getByAddress(quads);
    }
}