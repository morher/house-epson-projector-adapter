package net.morher.house.epson.projector;

import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import org.junit.Test;
import org.mockito.Mockito;

import net.morher.house.api.entity.Device;
import net.morher.house.api.entity.DeviceId;
import net.morher.house.api.entity.DeviceManager;
import net.morher.house.api.entity.EntityManager;
import net.morher.house.api.mqtt.client.HouseMqttClient;
import net.morher.house.epson.api.EscVpController;
import net.morher.house.epson.api.ProjectorPower;
import net.morher.house.test.client.TestHouseMqttClient;
import net.morher.house.test.schedule.TestHouseScheduler;
import net.morher.house.test.subscription.TestSubscriber;

public class ProjectorManagerTest {
    private final TestHouseScheduler scheduler = new TestHouseScheduler();
    private final HouseMqttClient client = TestHouseMqttClient.loopback();
    private final EntityManager entityManager = new EntityManager(client);
    private final DeviceManager deviceManager = new DeviceManager(entityManager);

    private final Device device = deviceManager.device(new DeviceId("Living room", "Projector"));
    private final EpsonProjectorDevice projectorDevice = new EpsonProjectorDevice(device);

    private final EscVpController controller = Mockito.mock(EscVpController.class);

    @Test
    public void testSyncPowerInterval() {
        doReturn(ProjectorPower.STANDBY_WITH_NETWORK).when(controller).queryPower();

        new ProjectorManager(projectorDevice, controller, scheduler);

        verifyNoInteractions(controller);

        scheduler.runWaitingTasks();
        verify(controller, times(1)).queryPower();

        scheduler.skipAhead(15, SECONDS);
        verify(controller, times(1)).queryPower();

        scheduler.runWaitingTasks();
        verify(controller, times(2)).queryPower();
    }

    @Test
    public void testSyncPowerReportNewState() {
        TestSubscriber<Boolean> powerSub = new TestSubscriber<>(projectorDevice.power().state());
        doReturn(ProjectorPower.STANDBY_WITH_NETWORK).when(controller).queryPower();

        new ProjectorManager(projectorDevice, controller, scheduler);
        scheduler.runWaitingTasks();

        assertThat(powerSub.size(), is(1));
        assertThat(powerSub.lastItem(), is(equalTo(false)));
        powerSub.reset();

        doReturn(ProjectorPower.LAMP_ON).when(controller).queryPower();
        scheduler.skipAhead(15, SECONDS).runWaitingTasks();

        assertThat(powerSub.size(), is(1));
        assertThat(powerSub.lastItem(), is(equalTo(true)));
    }

    @Test
    public void testPowerOnWithDelay() {
        TestSubscriber<Boolean> powerSub = new TestSubscriber<>(projectorDevice.power().state());
        doReturn(ProjectorPower.STANDBY_WITH_NETWORK).when(controller).queryPower();
        projectorDevice.power().state().publish(false);

        new ProjectorManager(projectorDevice, controller, scheduler);
        scheduler.runWaitingTasks();
        powerSub.reset();

        projectorDevice.power().command().publish(true);
        scheduler.runWaitingTasks();

        verify(controller, never()).powerOn();
        assertThat(powerSub.items(), hasItems(true));

        scheduler.skipAhead(3, SECONDS).runWaitingTasks();
        verify(controller).powerOn();
    }

    @Test
    public void testCancelledPowerOn() {
        TestSubscriber<Boolean> powerSub = new TestSubscriber<>(projectorDevice.power().state());
        doReturn(ProjectorPower.STANDBY_WITH_NETWORK).when(controller).queryPower();
        projectorDevice.power().state().publish(false);

        new ProjectorManager(projectorDevice, controller, scheduler);
        scheduler.runWaitingTasks();
        powerSub.reset();

        projectorDevice.power().command().publish(true);
        scheduler.skipAhead(2999, MILLISECONDS);
        assertThat(powerSub.lastItem(), is(true));

        projectorDevice.power().command().publish(false);
        scheduler.runWaitingTasks();
        assertThat(powerSub.lastItem(), is(false));

        scheduler.skipAhead(1, HOURS).runWaitingTasks();
        verify(controller, never()).powerOn();
    }

    @Test
    public void testPowerOffWithDelay() {
        TestSubscriber<Boolean> powerSub = new TestSubscriber<>(projectorDevice.power().state());
        doReturn(ProjectorPower.LAMP_ON).when(controller).queryPower();

        new ProjectorManager(projectorDevice, controller, scheduler);
        scheduler.runWaitingTasks();
        powerSub.reset();

        projectorDevice.power().command().publish(false);
        scheduler.runWaitingTasks();

        verify(controller, never()).powerOff();
        verify(controller).mute(eq(true));
        assertThat(powerSub.items(), hasItems(false));

        scheduler.skipAhead(15, SECONDS).runWaitingTasks();
        verify(controller).powerOff();
    }

    @Test
    public void testCancelledPowerOff() {
        TestSubscriber<Boolean> powerSub = new TestSubscriber<>(projectorDevice.power().state());
        doReturn(ProjectorPower.LAMP_ON).when(controller).queryPower();
        projectorDevice.power().state().publish(true);

        new ProjectorManager(projectorDevice, controller, scheduler);
        scheduler.runWaitingTasks();
        powerSub.reset();

        projectorDevice.power().command().publish(false);
        scheduler.skipAhead(14999, MILLISECONDS);
        assertThat(powerSub.lastItem(), is(false));
        verify(controller).mute(eq(true));

        projectorDevice.power().command().publish(true);
        scheduler.runWaitingTasks();

        verify(controller).mute(eq(false));
        assertThat(powerSub.lastItem(), is(true));

        scheduler.skipAhead(1, HOURS).runWaitingTasks();
        verify(controller, never()).powerOff();
    }

}
