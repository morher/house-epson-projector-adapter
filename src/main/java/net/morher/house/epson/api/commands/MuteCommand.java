package net.morher.house.epson.api.commands;

public enum MuteCommand implements EscVpCommand<Void> {
    ON(true), OFF(false);

    private final boolean mute;

    private MuteCommand(boolean mute) {
        this.mute = mute;
    }

    @Override
    public String getCommand() {
        return mute ? "MUTE ON" : "MUTE OFF";
    }
}
