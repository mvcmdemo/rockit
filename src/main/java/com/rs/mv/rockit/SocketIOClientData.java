package com.rs.mv.rockit;

import com.corundumstudio.socketio.SocketIOClient;

import java.util.concurrent.Future;

public class SocketIOClientData {
    private SSHClient ssh;
    private Future<?> receiverFuture;
    private SocketIOClient client;

    public SocketIOClientData(SSHClient ssh, Future<?> receiverFuture, SocketIOClient client) {
        this.ssh = ssh;
        this.receiverFuture = receiverFuture;
        this.client = client;
    }

    public SSHClient getSsh() {
        return ssh;
    }

    public void setSsh(SSHClient ssh) {
        this.ssh = ssh;
    }

    public Future<?> getReceiverFuture() {
        return receiverFuture;
    }

    public void setReceiverFuture(Future<?> receiverFuture) {
        this.receiverFuture = receiverFuture;
    }

    public SocketIOClient getClient() {
        return client;
    }

    public void setClient(SocketIOClient client) {
        this.client = client;
    }
}
