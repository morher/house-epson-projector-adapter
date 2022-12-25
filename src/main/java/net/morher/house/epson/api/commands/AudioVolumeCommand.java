package net.morher.house.epson.api.commands;

public class AudioVolumeCommand implements EscVpCommand<Void> {
    private final int volume;

    public AudioVolumeCommand(int volume) {
        this.volume = volume;
    }

    @Override
    public String getCommand() {
        return "VOL " + volume;
    }
}
