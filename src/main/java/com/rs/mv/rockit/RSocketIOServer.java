package com.rs.mv.rockit;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.jcraft.jsch.JSchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.Ostermiller.util.CircularByteBuffer;

import java.io.*;
import java.nio.charset.Charset;

@Service
public class RSocketIOServer {
    SocketIOServer server;
    Thread serverThread;
    Thread receiverThread;
    SSHClient sshClient;

    @Autowired
    public RSocketIOServer(SSHClient sshClient) throws Exception {
        this.sshClient = sshClient;
        startServer();
    }

    public void startServer() throws Exception {
        Configuration config = new Configuration();
        config.setHostname("localhost"); // Needs to be transformed to machine DNS name or IP-address
        config.setPort(8081);

        try {
            sshClient.connect();
        } catch (JSchException e) {
            e.printStackTrace();
            return;
        }

        final CircularByteBuffer sshSender = sshClient.getSendBuffer();
        final CircularByteBuffer sshReceiver = sshClient.getReceiveBuffer();

        server = new SocketIOServer(config);
        server.addConnectListener(new ConnectListener() {
            @Override
            public void onConnect(SocketIOClient socketIOClient) {
                System.out.println("Client connected");
                System.out.println(socketIOClient.getHandshakeData().getSingleUrlParam("machine_id"));
                startReceiver(socketIOClient, sshReceiver);
            }
        });
        server.addEventListener("testevent", Object.class, new DataListener<Object>() {
            @Override
            public void onData(SocketIOClient client, Object data, AckRequest ackRequest) {
                // broadcast messages to all clients
                server.getBroadcastOperations().sendEvent("testevent", data);
            }
        });

        server.addEventListener("data", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient client, String data, AckRequest ackRequest) {
                //startReceiver(client, sshReceiver);
                try {
                    sshSender.getOutputStream().write(data.getBytes(Charset.defaultCharset()));
                    //sshSender.getOutputStream().flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // broadcast messages to all clients
                //server.getBroadcastOperations().sendEvent("testevent", data);
                //System.out.println(data);
            }
        });

        Thread serverThread = new Thread(this::startServerInThread);
        serverThread.start();

    }

    public void stopServer() {
        if (serverThread != null) {
            serverThread.interrupt();
        }
        if (server != null) {
            server.stop();
        }
    }

    private void startServerInThread() {
        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        while (true) {
            try {
                Thread.sleep(Integer.MAX_VALUE);
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    private void startReceiver(SocketIOClient client, CircularByteBuffer sshReceiver) {
        if (receiverThread == null) {
            receiverThread = new Thread(() -> {
                BufferedReader reader = new BufferedReader(new InputStreamReader(sshReceiver.getInputStream()));
                while (true) {
                    //String line = "";
                    int value = -1;
                    try {
                        value = reader.read();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (value > -1) {
                        client.sendEvent("data", String.valueOf((char) value));
                    } else {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            return;
                        }
                    }

                }
            });
            receiverThread.start();
        }
    }
}
