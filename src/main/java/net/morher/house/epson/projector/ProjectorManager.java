package net.morher.house.epson.projector;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import net.morher.house.api.schedule.HouseScheduler;
import net.morher.house.epson.api.EscVpController;
import net.morher.house.epson.api.ProjectorPower;

public class ProjectorManager {
    private final EpsonProjectorDevice device;
    private final EscVpController escVpController;
    private final HouseScheduler scheduler;
    private final PowerDelayPolicy powerDelayPolicy = new PowerDelayPolicy(Duration.ofSeconds(3), Duration.ofSeconds(15));
    private boolean expectedPower;
    private boolean expectedAvmute;
    private PowerStateChangeRequest powerRequest;
    private Boolean avmuteRequest;

    public ProjectorManager(EpsonProjectorDevice device, EscVpController escVpController, HouseScheduler scheduler) {
        this.device = device;
        this.escVpController = escVpController;
        this.scheduler = scheduler;
        this.scheduler.scheduleWithFixedDelay(this::synchronizePowerState, 0, 15000, TimeUnit.MILLISECONDS);
        this.device.registerCommandListener(this);
    }

    public void commandPower(boolean power) {
        updatePowerState(power);
        powerRequest = new PowerStateChangeRequest(power, scheduler.now());
        scheduler.execute(this::synchronizePowerState);
    }

    public void commandAvMute(boolean avmute) {
        updateAvmuteState(avmute);
        avmuteRequest = avmute;
        scheduler.execute(this::synchronizePowerState);
    }

    public void commandVolume(double volume) {
        // TODO: Volume
        device.publishVolume(volume);
        ;
    }

    private void synchronizePowerState() {
        ProjectorPower power = escVpController.queryPower();
        if (!power.isReady()) {
            scheduler.schedule(this::synchronizePowerState, 2000, TimeUnit.MILLISECONDS);
            return;
        }

        boolean avmute = power.isOn() && power.isReady() && escVpController.queryMute();
        Instant now = scheduler.now();

        // Check if request is fulfilled
        if (powerRequest != null && powerRequest.isPower() == power.isOn()) {
            cancelPowerRequest(power);

        } else if (powerRequest != null) {
            handlePowerRequest(avmute, now);

        } else if (avmuteRequest != null) {
            handleAvmuteRequest(power, avmute);

        } else {
            updatePowerState(power.isOn());
            updateAvmuteState(avmute);
        }
        device.publishLampHours(escVpController.queryLampHours());
    }

    // Check if request should be performed (or schedule new sync)
    private void handlePowerRequest(boolean avmute, Instant now) {
        long millisToWait = powerDelayPolicy.millisToWait(powerRequest, now);
        if (millisToWait > 0) {
            if (!powerRequest.isPower() && !avmute) {
                escVpController.mute(true);
            }
            scheduler.schedule(this::synchronizePowerState, millisToWait, TimeUnit.MILLISECONDS);

        } else {
            if (powerRequest.isPower()) {
                escVpController.powerOn();
            } else {
                escVpController.powerOff();
            }
            powerRequest = null;
            scheduler.execute(this::synchronizePowerState);
            return;
        }
    }

    private void handleAvmuteRequest(ProjectorPower power, boolean avmute) {
        if (!power.isOn()) {
            updateAvmuteState(false);
        } else if (avmuteRequest != avmute) {
            escVpController.mute(avmuteRequest);
            avmuteRequest = null;
        }
    }

    private void cancelPowerRequest(ProjectorPower power) {
        updatePowerState(power.isOn());
        if (powerRequest.isPower() && !expectedAvmute) {
            escVpController.mute(expectedAvmute);
        }
        powerRequest = null;
        scheduler.execute(this::synchronizePowerState);
    }

    private void updatePowerState(boolean power) {
        expectedPower = power;
        device.publishPower(expectedPower);
        ;
    }

    private void updateAvmuteState(boolean avmute) {
        expectedAvmute = avmute;
        device.publishAvMute(expectedAvmute);
    }

    private static class PowerStateChangeRequest {
        private final boolean power;
        private final Instant requestTime;

        public PowerStateChangeRequest(boolean power, Instant requestTime) {
            this.power = power;
            this.requestTime = requestTime;
        }

        public boolean isPower() {
            return power;
        }

        public Instant getRequestTime() {
            return requestTime;
        }
    }

    private static class PowerDelayPolicy {
        private final Duration powerOnDelay;
        private final Duration powerOffDelay;

        public PowerDelayPolicy(Duration powerOnDelay, Duration powerOffDelay) {
            this.powerOnDelay = powerOnDelay;
            this.powerOffDelay = powerOffDelay;
        }

        public long millisToWait(PowerStateChangeRequest request, Instant now) {
            Instant endTime = request
                    .getRequestTime()
                    .plus(request.isPower()
                            ? powerOnDelay
                            : powerOffDelay);

            return ChronoUnit.MILLIS.between(now, endTime);
        }
    }
}
