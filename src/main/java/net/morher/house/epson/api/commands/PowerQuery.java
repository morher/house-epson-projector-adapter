package net.morher.house.epson.api.commands;

import net.morher.house.epson.api.ProjectorPower;
import net.morher.house.epson.api.ResponseData;

public class PowerQuery implements EscVpCommand<ProjectorPower> {

    @Override
    public String getCommand() {
        return "PWR?";
    }

    @Override
    public ProjectorPower parseResponse(ResponseData response) {
        String mode = response
                .prefixedValue("PWR=")
                .toString();

        switch (mode) {
        case "00":
            return ProjectorPower.STANDBY;

        case "01":
            return ProjectorPower.LAMP_ON;

        case "02":
            return ProjectorPower.WARMUP;

        case "03":
            return ProjectorPower.COOLDOWN;

        case "04":
            return ProjectorPower.STANDBY_WITH_NETWORK;

        case "05":
            return ProjectorPower.ABNORMAL_STANDBY;

        }

        return ProjectorPower.UNKNOWN;
    }

}
