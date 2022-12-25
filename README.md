# Epson Projector adapter
Control Epson projectors throught the ESC/VP.net protocol.

The adapter takes commands through MQTT, synchronizes with the projector and reports the status back.
Turning on the projector directly from the device will result in a status-update on MQTT.

When controlling the power state of the projector through MQTT there is a delay to not wear out the bulb on unintended changes. Powerin up is delayed for 3 seconds, to give the operator a chance to cancel. When turning off the avmute is turned on at once, then the projector is turnes off after 30 seconds. Switching it on again during this period will turn off avmute and cancel the timer.

## Configuration
Each projector is identified by it's house device-id and the ip-address must also be provided.

### Example
```yaml
epson:
   projectors:
      -  device:
            room: 'Living room'
            name: 'Projector'
         ip: '192.186.1.150'
```
