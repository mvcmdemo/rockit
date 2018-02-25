package com.rs.mv.rockit;

import com.sun.jna.platform.win32.Crypt32Util;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Formatter;

@Service
public class RDPGenerator {
    private Machine machine;

    public void setMachine(Machine machine) {
        this.machine = machine;
    }

    public String getRDP() throws Exception {
        if (machine == null) {
            throw new Exception("Machine is not specified");
        }

        StringBuilder content = new StringBuilder();
        content.append(String.format("full address:s:%s", machine.getHost()));
        content.append("\n");
        content.append(String.format("username:s:%s", machine.getUser()));
        content.append("\n");
        content.append(String.format("password 51:b:%s", encryptRdpPassword(machine.getPassword())));

        return content.toString();
    }

    private static String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb);
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }
        formatter.close();
        return sb.toString();
    }

    private String encryptRdpPassword(String password) throws UnsupportedEncodingException {
        return toHexString(Crypt32Util.cryptProtectData(password.getBytes("UTF-16LE"), null, 0, "psw", null));
    }}
