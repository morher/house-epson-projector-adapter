package net.morher.house.epson.api.commands;

import net.morher.house.epson.api.ResponseData;

public enum MuteQuery implements EscVpCommand<Boolean> {
    INSTANCE;

    @Override
    public String getCommand() {
        return "MUTE?";
    }

    @Override
    public Boolean parseResponse(ResponseData response) {
        String mode = response
                .prefixedValue("MUTE=")
                .toString();

        switch (mode) {
        case "ON":
            return true;

        case "OFF":
            return false;

        }

        return false;
    }

}
