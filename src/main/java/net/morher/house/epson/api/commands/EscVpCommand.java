package net.morher.house.epson.api.commands;

import net.morher.house.epson.api.ResponseData;

public interface EscVpCommand<R> {
    String getCommand();

    default R parseResponse(ResponseData response) {
        return null;
    }
}