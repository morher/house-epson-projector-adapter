package net.morher.house.epson.projector;

import net.morher.house.api.context.HouseAdapter;
import net.morher.house.api.context.HouseMqttContext;
import net.morher.house.api.entity.DeviceManager;
import net.morher.house.epson.projector.config.EpsonProjectorAdapterConfig;

public class EpsonProjectorAdapter implements HouseAdapter {
    public static void main(String[] args) {
        new EpsonProjectorAdapter()
                .run(new HouseMqttContext("epson-projector-adapter"));
    }

    @Override
    public void run(HouseMqttContext ctx) {
        DeviceManager deviceManager = ctx.deviceManager();

        new EpsonProjectorController(deviceManager)
                .configure(ctx.loadAdapterConfig(EpsonProjectorAdapterConfig.class).getEpson());
    }
}
