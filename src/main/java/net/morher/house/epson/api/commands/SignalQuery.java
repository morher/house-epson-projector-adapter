package net.morher.house.epson.api.commands;

import net.morher.house.epson.api.ResponseData;

public class SignalQuery implements EscVpCommand<String> {

    @Override
    public String getCommand() {
        return "SIGNAL?";
    }

    @Override
    public String parseResponse(ResponseData response) {
        return response
                .prefixedValue("SIGNAL=")
                .toString();
    }
}
