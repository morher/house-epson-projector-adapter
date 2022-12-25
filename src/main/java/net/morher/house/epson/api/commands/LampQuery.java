package net.morher.house.epson.api.commands;

import net.morher.house.epson.api.ResponseData;

public class LampQuery implements EscVpCommand<Integer> {

    @Override
    public String getCommand() {
        return "LAMP?";
    }

    @Override
    public Integer parseResponse(ResponseData response) {
        return response.prefixedValue("LAMP=").asInteger();
    }

}
