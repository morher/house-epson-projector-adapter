package net.morher.house.epson.api.commands;

import net.morher.house.epson.api.ResponseData;

public class AudioVolumeQuery implements EscVpCommand<Integer> {

    @Override
    public String getCommand() {
        return "VOL?";
    }

    @Override
    public Integer parseResponse(ResponseData response) {
        return response
                .prefixedValue("VOL=")
                .asInteger();
    }

}
