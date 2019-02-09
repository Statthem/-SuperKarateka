package com.mygdx.game.UDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;

public class UDP_Client {
    private AsyncTask<Void, Void, Void> async_cient;
    public String Message;

    @SuppressLint("NewApi")
    public void NachrichtSenden() {
        async_cient = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                DatagramSocket ds = null;

                try {
                    InetAddress addr = InetAddress.getByName("213.110.160.99");
                    ds = new DatagramSocket();
                    DatagramPacket dp;
                    dp = new DatagramPacket(Message.getBytes(), Message.length(),addr,5050);
                    ds.send(dp);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (ds != null) {
                        ds.close();
                    }
                }
                return null;
            }

            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
            }
        };

        if (Build.VERSION.SDK_INT >= 11)
            async_cient.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else async_cient.execute();
    }
}