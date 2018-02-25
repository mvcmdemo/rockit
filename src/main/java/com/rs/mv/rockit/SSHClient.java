package com.rs.mv.rockit;

import com.Ostermiller.util.CircularByteBuffer;
import com.jcraft.jsch.*;
import com.rs.mv.rockit.exception.SSHException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Lazy
@Scope("prototype")
public class SSHClient {
    private final static int CONNECTION_TIMEOUT = 10000;

    private String host;
    private int port = 22;
    private String userName = "root";
    private String password;
    private CircularByteBuffer sendBuffer;
    private CircularByteBuffer receiveBuffer;
    private JSch ssh;
    private Session sshSession;
    private Channel channel;

    @Autowired
    public SSHClient(JSch ssh) {
        this.ssh = ssh;
    }

    public void connect() throws SSHException {
        if (host == null || host.isEmpty()) {
            throw new SSHException("Host is not specified.");
        }

        try {
            sshSession = ssh.getSession(userName, host, port);
        } catch (JSchException e) {
            throw new SSHException(String.format("Unable to get session. %s", e.getMessage()), e);
        }

        sshSession.setPassword(password);
        sshSession.setUserInfo(new UserInfo() {
            @Override
            public String getPassphrase() {
                return null;
            }

            @Override
            public String getPassword() {
                return null;
            }

            @Override
            public boolean promptPassword(String s) {
                return false;
            }

            @Override
            public boolean promptPassphrase(String s) {
                return false;
            }

            @Override
            public boolean promptYesNo(String s) {
                return true;
            }

            @Override
            public void showMessage(String s) {
                //
            }
        });

        try {
            sshSession.connect(CONNECTION_TIMEOUT);
        } catch (JSchException e) {
            throw new SSHException(String.format("Unable to connect session. %s", e.getMessage()), e);
        }

        CircularByteBuffer sendBuffer = new CircularByteBuffer();
        CircularByteBuffer receiveBuffer = new CircularByteBuffer();

        try {
            channel = sshSession.openChannel("shell");
        } catch (JSchException e) {
            throw new SSHException(String.format("Unable to open channel. %s", e.getMessage()), e);
        }
        channel.setInputStream(sendBuffer.getInputStream());
        channel.setOutputStream(receiveBuffer.getOutputStream());
        try {
            channel.connect(CONNECTION_TIMEOUT);
        } catch (JSchException e) {
            throw new SSHException(String.format("Unable to connect channel. %s", e.getMessage()), e);
        }
        this.receiveBuffer = receiveBuffer;
        this.sendBuffer = sendBuffer;
    }

    public void disconnect() {
        if (channel != null && channel.isConnected()) {
            channel.disconnect();
        }
        if (sshSession != null && sshSession.isConnected()) {
            sshSession.disconnect();
        }
    }

    public CircularByteBuffer getSendBuffer() throws Exception {
        if (!isConnected()) {
            throw new Exception("Not connected!");
        }
        return sendBuffer;
    }

    public CircularByteBuffer getReceiveBuffer() throws Exception {
        if (!isConnected()) {
            throw new Exception("Not connected!");
        }
        return receiveBuffer;
    }

    public boolean isConnected() {
        return (sshSession.isConnected() && channel.isConnected());
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
