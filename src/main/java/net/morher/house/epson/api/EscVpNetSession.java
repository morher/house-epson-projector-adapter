package net.morher.house.epson.api;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;

public class EscVpNetSession {
    private static final byte[] HANDSHAKE_REQUEST = new byte[] { 'E', 'S', 'C', '/', 'V', 'P', '.', 'n', 'e', 't', 0x10, 0x03, 0x00, 0x00, 0x00, 0x00 };
    private static final byte[] EXPECTED_HANDSHAKE_RESPONSE = new byte[] { 'E', 'S', 'C', '/', 'V', 'P', '.', 'n', 'e', 't', 0x10, 0x03, 0x00, 0x00, 0x20, 0x00 };
    private Socket _socket;
    private final byte[] buffer = new byte[20];
    private int pos = 0;
    private int len = 0;
    private boolean ready;

    public EscVpNetSession(InetAddress target) {
        try {
            _socket = new Socket(target, 3629);
            performHandshake();

        } catch (IOException e) {
            throw new IllegalStateException("Could not connect to the device", e);
        }
    }

    private void performHandshake() throws IOException {
        OutputStream out = _socket.getOutputStream();
        out.write(HANDSHAKE_REQUEST);
        out.flush();

        byte[] buffer = new byte[16];
        int len = _socket.getInputStream().read(buffer);
        if (len != 16 || !Arrays.equals(buffer, EXPECTED_HANDSHAKE_RESPONSE)) {
            throw new IllegalStateException("Invalid handshake");
        }
        ready = true;
    }

    public String query(String requestData) {
        waitForReady();
        sendRequest(requestData);
        return readResponse();
    }

    private void waitForReady() {
        if (!ready) {
            if (nextByte() != ':') {
                throw new IllegalStateException("Communication out of sync with the device.");
            }
            ready = true;
        }
    }

    public void sendRequest(String requestData) {
        ready = false;
        try {
            OutputStream out = _socket.getOutputStream();
            String request = requestData + "\r";
            out.write(request.getBytes());
            out.flush();

        } catch (IOException e) {
            throw new IllegalStateException("Failed to send command " + requestData + " to the device");
        }
    }

    public String readResponse() {
        byte b = nextByte();
        if (b == ':') {
            ready = true;
            return "";
        }
        StringBuffer sb = new StringBuffer();
        while (b != 0x0D) {
            sb.append((char) b);
            b = nextByte();
        }
        return sb.toString();
    }

    private byte nextByte() {
        if (pos >= len) {
            pos = 0;
            try {
                len = _socket.getInputStream().read(buffer);

            } catch (IOException e) {
                throw new IllegalStateException("Communication with the device failed", e);
            }
            if (len <= 0) {
                throw new IllegalStateException("No data received from the device");
            }
        }
        return buffer[pos++];
    }
}
