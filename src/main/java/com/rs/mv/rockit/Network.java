package com.rs.mv.rockit;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetAddress;

@Service
public class Network {
    public boolean pingHost(String host, int timeout) throws IOException {
        InetAddress address = InetAddress.getByName(host);
        return address.isReachable(timeout);
    }
}
