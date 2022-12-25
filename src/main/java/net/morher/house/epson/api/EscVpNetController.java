package net.morher.house.epson.api;

import java.net.InetAddress;

import net.morher.house.epson.api.commands.EscVpCommand;

public class EscVpNetController implements EscVpController {
    private final InetAddress target;
    private EscVpNetSession _session;

    public EscVpNetController(InetAddress target) {
        this.target = target;
    }

    public <R> R sendCommand(EscVpCommand<R> command) {
        try {
            String requestData = command.getCommand();
            String response = getSession().query(requestData);
            if (response.startsWith("ERR")) {
                throw new IllegalArgumentException("Invalid command: " + requestData);
            }
            return command.parseResponse(new ResponseData(response, 0));

        } catch (IllegalStateException e) {
            _session = null;
            throw e;
        }
    }

    private EscVpNetSession getSession() {
        if (_session == null) {
            _session = new EscVpNetSession(target);
        }
        return _session;
    }
}
