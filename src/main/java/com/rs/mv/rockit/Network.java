package com.rs.mv.rockit;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.*;

@Service
public class Network {
    public boolean pingHost(String host, int timeout) throws IOException {
        InetAddress address = InetAddress.getByName(host);
        return address.isReachable(timeout);
    }

    public MachinePlatforms getHostPlatform(String host, int connectTimeout) {
        final int winRDPPort = 3389;
        final int nixSSHPort = 22;
        if (isRemotePortOpen(host, winRDPPort, connectTimeout)) {
            return MachinePlatforms.WINDOWS;
        }
        if (isRemotePortOpen(host, nixSSHPort, connectTimeout)) {
            return MachinePlatforms.UNIX_LINUX;
        }
        return MachinePlatforms.UNKNOWN;
    }

    public boolean isRemotePortOpen(String host, int port, int timeout) {
        Socket s = null;
        String reason = null;
        boolean portOpen = false;
        try {
            s = new Socket();
            s.setReuseAddress(true);
            SocketAddress sa = new InetSocketAddress(host, port);
            s.connect(sa, timeout);
        } catch (IOException e) {
            // ignoring
        } finally {
            if (s != null) {
                if (s.isConnected()) {
                    portOpen = true;
                }
                try {
                    s.close();
                } catch (IOException e) {
                    // ignoring
                }
            }
        }
        return portOpen;
    }

    String getLocalHostName() {
        String hostName;
        try  {
            InetAddress addr;
            addr = InetAddress.getLocalHost();
            hostName = addr.getHostName();
        } catch (UnknownHostException ex) {
            return "localhost";
        }
        return hostName;
    }
}
