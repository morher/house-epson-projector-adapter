package net.morher.house.epson.api.commands;

public enum PowerCommand implements EscVpCommand<Void> {
    ON(true), OFF(false);

    private final boolean power;

    private PowerCommand(boolean power) {
        this.power = power;
    }

    @Override
    public String getCommand() {
        return power ? "PWR ON" : "PWR OFF";
    }
}
