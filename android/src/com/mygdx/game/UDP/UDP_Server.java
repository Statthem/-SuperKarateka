package com.mygdx.game.UDP;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;

import com.mygdx.game.MainActivity;

public class UDP_Server{

    public static Intent intent;
    public static AppCompatActivity activity;
    private AsyncTask<Void, Void, Void> async;
    private boolean Server_aktiv = true;

    @SuppressLint("NewApi")
    public void runUdpServer()
    {
        async = new AsyncTask<Void, Void, Void>()
        {
            @Override
            protected Void doInBackground(Void... params)
            {
                byte[] lMsg = new byte[4096];
                DatagramPacket dp = new DatagramPacket(lMsg, lMsg.length);
                DatagramSocket ds = null;

                try
                {
                    ds = new DatagramSocket(null);

                    InetAddress addr = InetAddress.getByName("localhost");
                    ds.bind(new InetSocketAddress(InetAddress.getByName("192.168.10.3"),5050));
                    ds.send(dp);

                    while(Server_aktiv)
                    {
                        ds.receive(dp);
                        System.out.println("ORAORAORAORAORAORA");
                        System.out.println(new String(lMsg, 0, dp.getLength()));
                        intent.putExtra(MainActivity.EXTRA_MESSAGE,new String(lMsg, 0, dp.getLength()));
                        activity.startActivity(intent);
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    if (ds != null)
                    {
                        ds.close();
                    }
                }

                return null;
            }
        };

        if (Build.VERSION.SDK_INT >= 11) async.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else async.execute();
    }

    public void stop_UDP_Server(){
        Server_aktiv = false;
    }
}