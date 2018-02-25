package com.rs.mv.rockit;

import com.Ostermiller.util.CircularByteBuffer;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class RSocketIOServer {
    private final static int SOCKET_IO_SERVER_PORT = 8081; // TODO: move to configuration
    private final static String MACHINE_ID_PARAM_NAME = "machine_id";

    private String serverName;
    private ApplicationContext ctx;
    private MachineService machineService;
    private SocketIOServer server;
    private ExecutorService receiverThreadPool;
    private Map<UUID, SocketIOClientData> clients = new ConcurrentHashMap<>();
    private Thread serverThread;

    @Autowired
    public RSocketIOServer(ApplicationContext ctx, MachineService machineService, Network network) {
        this.ctx = ctx;
        this.machineService = machineService;
        //this.serverName = network.getLocalHostName();
        this.serverName = "localhost";
        receiverThreadPool = Executors.newCachedThreadPool();
        configureServer();
        startServer();
    }

    private void configureServer() {
        Configuration config = new Configuration();
        config.setHostname(serverName);
        config.setPort(SOCKET_IO_SERVER_PORT);
        server = new SocketIOServer(config);
        server.addConnectListener(this::clientConnected);
        server.addEventListener("data", String.class, this::dataEventProcess);
        server.addDisconnectListener(this::clientDisconnected);
    }

    private void startServer() {
        if (serverThread == null) {
            serverThread = new Thread(this::startServerInThread);
        }
        serverThread.start();
    }

    private void clientConnected(SocketIOClient socketIOClient) {
        String machinedId = socketIOClient.getHandshakeData().getSingleUrlParam(MACHINE_ID_PARAM_NAME);
        if (machinedId == null || machinedId.isEmpty()) {
            sendError(socketIOClient, String.format("Invalid machine ID specified: %s", machinedId));
            return;
        }
        long id = Long.parseLong(machinedId);
        Machine machine;
        try {
            machine = machineService.getById(id);
        } catch (Exception e) {
            sendError(socketIOClient, String.format("Unable to get machine by ID. %s", e.getMessage()));
            return;
        }
        if (machine == null) {
            sendError(socketIOClient, String.format("Unable to get machine by ID: %s", machinedId));
            return;
        }

        SSHClient ssh = ctx.getBean("SSHClient", SSHClient.class);
        ssh.setHost(machine.getHost());
        ssh.setUserName(machine.getUser());
        ssh.setPassword(machine.getPassword());

        try {
            ssh.connect();
        } catch (Exception e) {
            sendError(socketIOClient, String.format("Unable to establish SSH connection. %s", e.getMessage()));
            return;
        }
        CircularByteBuffer receiverBuffer;
        try {
            receiverBuffer = ssh.getReceiveBuffer();
        } catch (Exception e) {
            sendError(socketIOClient, "Unable to get buffer.");
            return;
        }
        Future<?> receiverFuture = receiverThreadPool.submit(() -> receiveFromSSH(socketIOClient, receiverBuffer));
        SocketIOClientData clientData = new SocketIOClientData(ssh, receiverFuture, socketIOClient);
        clients.put(socketIOClient.getSessionId(), clientData);
    }

    private void clientDisconnected(SocketIOClient socketIOClient) {
        SocketIOClientData clientData = clients.get(socketIOClient.getSessionId());
        if (clientData != null) {
            clientData.getReceiverFuture().cancel(true);
            if (clientData.getSsh().isConnected()) {
                clientData.getSsh().disconnect();
            }
        }
        clients.remove(socketIOClient.getSessionId());
    }

    @SuppressWarnings("unused")
    private void dataEventProcess(SocketIOClient client, String data, AckRequest ackRequest) {
        SocketIOClientData clientData = clients.get(client.getSessionId());
        if (clientData == null) {
            sendError(client, "Unable to get client session data.");
            return;
        }
        SSHClient ssh = clientData.getSsh();
        try {
            ssh.getSendBuffer().getOutputStream().write(data.getBytes(Charset.defaultCharset()));
            ssh.getSendBuffer().getOutputStream().flush();
        } catch (Exception e) {
            sendError(client, String.format("Error writing data to SSH connection. %s", e.getMessage()));
        }
    }

    private void sendError(SocketIOClient client, String message) {
        client.sendEvent("error", message);
        client.disconnect();
    }

    private void startServerInThread() {
        try {
            server.start();
        } catch (Exception e) {
            // TODO: process error somehow
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

    private void receiveFromSSH(SocketIOClient client, CircularByteBuffer sshReceiver) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(sshReceiver.getInputStream()));
        while (true) {
            int value;
            try {
                value = reader.read();
            } catch (IOException e) {
                sendError(client, String.format("Unable to read data from SSH connection. %s", e.getMessage()));
                return;
            }
            if (value > -1) {
                client.sendEvent("data", String.valueOf((char) value));
            } else {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    }

//    public void stopServer() {
//        if (serverThread != null) {
//            serverThread.interrupt();
//        }
//        if (server != null) {
//            server.stop();
//        }
//    }
}
