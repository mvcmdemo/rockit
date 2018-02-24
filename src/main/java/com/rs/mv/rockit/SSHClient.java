package com.rs.mv.rockit;

import com.Ostermiller.util.CircularByteBuffer;
import com.Ostermiller.util.CircularCharBuffer;
import com.jcraft.jsch.*;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class SSHClient {
    private static String host = "den-vm-lxsb1.u2lab.rs.com";
    private static int port = 22;
    private static String userName = "upix";
    private static String password = "U2rivers";

    private boolean connected = false;
    private CircularByteBuffer sendBuffer;
    private CircularByteBuffer receiveBuffer;
    private JSch ssh = new JSch();

    public void connect() throws JSchException {
        connected = false;
        Session sshSession;
        sshSession = ssh.getSession(userName, host, port);
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
                System.out.println(s);
            }
        });

        sshSession.connect(10000);   // making a connection with timeout.
        CircularByteBuffer sendBuffer = new CircularByteBuffer();
        CircularByteBuffer receiveBuffer = new CircularByteBuffer();
        Channel channel = sshSession.openChannel("shell");
        channel.setInputStream(sendBuffer.getInputStream());
        //channel.setInputStream(System.in);
        channel.setOutputStream(receiveBuffer.getOutputStream());
        //channel.setOutputStream(System.out);
        channel.connect(10000);
        //channel.setInputStream(new ByteArrayInputStream("ls\n".getBytes()));

        connected = true;
        this.receiveBuffer = receiveBuffer;
        this.sendBuffer = sendBuffer;
    }

    public CircularByteBuffer getSendBuffer() throws Exception {
        if (!connected) {
            throw new Exception("Not connected!");
        }
        return sendBuffer;
    }

    public void setSendBuffer(CircularByteBuffer sendBuffer) {
        this.sendBuffer = sendBuffer;
    }

    public CircularByteBuffer getReceiveBuffer() throws Exception {
        if (!connected) {
            throw new Exception("Not connected!");
        }
        return receiveBuffer;
    }

    public void setReceiveBuffer(CircularByteBuffer receiveBuffer) {
        this.receiveBuffer = receiveBuffer;
    }

    public static void main (String [] args) {
        JSch ssh = new JSch();
        Session sshSession;
        try {
            sshSession = ssh.getSession(userName, host, port);
        } catch (JSchException e) {
            e.printStackTrace();
            return;
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
                System.out.println(s);
            }
        });

        try {
            sshSession.connect(10000);   // making a connection with timeout.
        } catch (JSchException e) {
            e.printStackTrace();
            return;
        }

        Channel channel;
        try {
            channel = sshSession.openChannel("shell");
        } catch (JSchException e) {
            e.printStackTrace();
            return;
        }
        channel.setInputStream(System.in);
        channel.setOutputStream(System.out);

        try {
            channel.connect(10000);
        } catch (JSchException e) {
            e.printStackTrace();
            return;
        }
    }

}
