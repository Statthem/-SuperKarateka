package com.mygdx.game.test;

import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class IPTest {

    @Test
    public void checkLocalIp () throws UnknownHostException {
        InetAddress addr = InetAddress.getByName("localhost");
        System.out.println(addr.toString());
    }
}
