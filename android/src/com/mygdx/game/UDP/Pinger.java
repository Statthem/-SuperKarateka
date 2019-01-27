package com.mygdx.game.UDP;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class Pinger extends Thread {
    private boolean running;

    List<String> ips = Collections.synchronizedList(new LinkedList<String>());
    UDPHelper udp;

    Context context;

    public Pinger(Context context){
        this.context = context;
    }

    @Override
    public void run() {
        try {
            udp = new UDPHelper(context, new UDPHelper.BroadcastListener() {
                @Override
                public void onReceive(String msg, String ip) {
                    Log.v(TAG, "receive message "+msg+" from "+ip);
                    if (!ips.contains(ip)) ips.add(ip);
                }
            });

            udp.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        running = true;
        while (running) {
            try {
                udp.send("!PING!");
                Log.v(TAG, "ping sended ....");
                sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void end() {
        running = false;
        udp.end();
    }
}