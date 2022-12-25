package net.morher.house.epson.api.commands;

import net.morher.house.epson.api.ResponseData;

public class EcoModeQuery implements EscVpCommand<Boolean> {

    @Override
    public String getCommand() {
        return "LUMINANCE?";
    }

    @Override
    public Boolean parseResponse(ResponseData response) {
        String luminance = response
                .prefixedValue("LUMINANCE=")
                .toString();
        return "01".equals(luminance);
    }

}
