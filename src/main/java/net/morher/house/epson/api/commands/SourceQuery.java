package net.morher.house.epson.api.commands;

import net.morher.house.epson.api.ResponseData;

public class SourceQuery implements EscVpCommand<String> {

    @Override
    public String getCommand() {
        return "SOURCE?";
    }

    @Override
    public String parseResponse(ResponseData response) {
        return response
                .prefixedValue("SOURCE=")
                .toString();
    }

}
