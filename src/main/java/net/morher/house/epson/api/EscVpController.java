package net.morher.house.epson.api;

import net.morher.house.epson.api.commands.EscVpCommand;
import net.morher.house.epson.api.commands.LampQuery;
import net.morher.house.epson.api.commands.MuteCommand;
import net.morher.house.epson.api.commands.MuteQuery;
import net.morher.house.epson.api.commands.PowerCommand;
import net.morher.house.epson.api.commands.PowerQuery;

public interface EscVpController {
    <R> R sendCommand(EscVpCommand<R> command);

    default ProjectorPower queryPower() {
        return sendCommand(new PowerQuery());
    }

    default void powerOn() {
        sendCommand(PowerCommand.ON);
    }

    default void powerOff() {
        sendCommand(PowerCommand.OFF);
    }

    default boolean queryMute() {
        return sendCommand(MuteQuery.INSTANCE);
    }

    default void mute(boolean doMute) {
        sendCommand(
                doMute
                        ? MuteCommand.ON
                        : MuteCommand.OFF);
    }

    default int queryLampHours() {
        return sendCommand(new LampQuery());
    }
}
