package net.morher.house.epson.projector.config;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import net.morher.house.api.config.DeviceName;

@Data
public class EpsonConfig {
    private final List<EpsonProjectorConfig> projectors = new ArrayList<>();

    @Data
    public static class EpsonProjectorConfig {
        private DeviceName device;
        private String ip;
    }

}