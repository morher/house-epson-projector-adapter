package net.morher.house.epson.api;

public enum ProjectorPower {
    STANDBY(false, true),
    LAMP_ON(true, true),
    WARMUP(true, false),
    COOLDOWN(false, false),
    STANDBY_WITH_NETWORK(false, true),
    ABNORMAL_STANDBY(false, false),
    UNKNOWN(false, false);

    private final boolean on;
    private final boolean ready;

    private ProjectorPower(boolean on, boolean ready) {
        this.on = on;
        this.ready = ready;
    }

    public boolean isOn() {
        return on;
    }

    public boolean isReady() {
        return ready;
    }
}
