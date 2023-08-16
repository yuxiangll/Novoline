package cc.novoline.modules.combat;

import cc.novoline.events.EventTarget;
import cc.novoline.events.events.BlockAABBEvent;
import cc.novoline.events.events.PacketEvent;
import cc.novoline.events.events.TickUpdateEvent;
import cc.novoline.gui.screen.setting.Setting;
import cc.novoline.gui.screen.setting.SettingType;
import cc.novoline.modules.AbstractModule;
import cc.novoline.modules.EnumModuleType;
import cc.novoline.modules.ModuleManager;
import cc.novoline.modules.configurations.annotation.Property;
import cc.novoline.modules.configurations.property.object.BooleanProperty;
import cc.novoline.modules.configurations.property.object.IntProperty;
import cc.novoline.modules.configurations.property.object.PropertyFactory;
import cc.novoline.modules.configurations.property.object.StringProperty;
import cc.novoline.modules.exploits.Blink;
import cc.novoline.modules.move.Speed;
import cc.novoline.utils.DebugUtil;
import net.minecraft.block.BlockAir;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.util.AxisAlignedBB;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.lwjgl.input.Keyboard;

import java.util.concurrent.ThreadLocalRandom;

import static cc.novoline.gui.screen.setting.Manager.put;
import static cc.novoline.modules.configurations.property.object.PropertyFactory.booleanFalse;
import static cc.novoline.modules.configurations.property.object.PropertyFactory.createInt;

public final class Velocity extends AbstractModule {
    @Property("mode")
    private final StringProperty velocity_mode = PropertyFactory.createString("Simple").acceptableValues("Simple", "Karhu");
    /* properties @off */
    @Property("alerts")
    private final BooleanProperty alerts = booleanFalse();
    @Property("horizontal")
    private final IntProperty horizontal = createInt(0).minimum(0).maximum(100);
    @Property("vertical")
    private final IntProperty vertical = createInt(0).minimum(0).maximum(100);
    @Property("chance")
    private final IntProperty chance = createInt(100).minimum(0).maximum(100);

    /* constructors @on */
    public Velocity(@NonNull ModuleManager moduleManager) {
        super(moduleManager, "Velocity", "Velocity", Keyboard.KEY_NONE, EnumModuleType.COMBAT, "Don't take knockback");
        put(new Setting("MODE","Mode",SettingType.COMBOBOX,this,velocity_mode));
        put(new Setting("ALERTS", "Alerts", SettingType.CHECKBOX, this, alerts,()->velocity_mode.get().equals("Simple")));
        put(new Setting("VEL_HOR", "Horizontal", SettingType.SLIDER, this, horizontal, 5,()->velocity_mode.get().equals("Simple")));
        put(new Setting("VEL_VER", "Vertical", SettingType.SLIDER, this, vertical, 5,()->velocity_mode.get().equals("Simple")));
        put(new Setting("VEL_CHANCE", "Chance", SettingType.SLIDER, this, chance, 5,()->velocity_mode.get().equals("Simple")));
    }

    /* methods */
    public boolean shouldCancel() {
        return isEnabled(Blink.class) || horizontal.get().equals(0) && vertical.get().equals(0) || isEnabled(Speed.class);
    }
    @Override
    public void onDisable(){
        super.onDisable();
    }
    @EventTarget
    public void onKarhuVelocity(BlockAABBEvent event){
        //if (getParent().onSwing.getValue() || getParent().onSprint.getValue() && !mc.thePlayer.isSwingInProgress) return;

        if (event.getBlock() instanceof BlockAir && mc.player.hurtTime > 0 && mc.player.ticksSinceVelocity <= 9) {
            final double x = event.getBlockPos().getX(), y = event.getBlockPos().getY(), z = event.getBlockPos().getZ();

            if (y == Math.floor(mc.player.posY) + 1) {
                event.setBoundingBox(AxisAlignedBB.fromBounds(0, 0, 0, 1, 0, 1).offset(x, y, z));
            }
        }
    }

    @EventTarget
    public void onVelocity(PacketEvent event) {
        switch (velocity_mode.get()) {
            case "Simple":{
                if (event.getState().equals(PacketEvent.State.INCOMING)) {
                    if (event.getPacket() instanceof S12PacketEntityVelocity) {
                        S12PacketEntityVelocity packet = (S12PacketEntityVelocity) event.getPacket();

                        if (packet.getEntityID() == mc.player.getEntityID()) {
                            if (!shouldCancel()) {
                                if (Math.random() <= chance.get() / 100) {
                                    packet.setMotionX(packet.getMotionX() * horizontal.get() / 100);
                                    packet.setMotionY(packet.getMotionY() * vertical.get() / 100);
                                    packet.setMotionZ(packet.getMotionZ() * horizontal.get() / 100);
                                } else {
                                    packet.setMotionX(packet.getMotionX());
                                    packet.setMotionY(packet.getMotionY());
                                    packet.setMotionZ(packet.getMotionZ());
                                }

                            } else {
                                event.setCancelled(true);
                            }

                            if (alerts.get()) {
                                DebugUtil.log("Velocity", String.valueOf(ThreadLocalRandom.current().nextInt(1000, 10000)));
                            }
                        }
                    }
                }
                break;
            } case "Karhu":{
                if (mc == null || mc.world == null || mc.getNetHandler() == null) return;
                Packet<?> packet = event.getPacket();
                if (packet instanceof S12PacketEntityVelocity) {
                    final S12PacketEntityVelocity wrapper = (S12PacketEntityVelocity) packet;
                    Entity entity = mc.world.getEntityByID(wrapper.getEntityID());
                    if (entity == null)return;
                    entity.ticksSinceVelocity = 0;
                    if (wrapper.getMotionY() / 8000.0D > 0.1 && Math.hypot(wrapper.getMotionZ() / 8000.0D, wrapper.getMotionX() / 8000.0D) > 0.2) {
                        entity.ticksSincePlayerVelocity = 0;
                    }
                }
//                } else if (packet instanceof S08PacketPlayerPosLook && mc.getNetHandler().doneLoadingTerrain) {
//                    mc.player.ticksSinceTeleport = 0;
//                }

                break;
            }
        }
    }

    @EventTarget
    private void onExplosion(PacketEvent event) {
        switch (velocity_mode.get()){
            case "Simple":{
                if (event.getState().equals(PacketEvent.State.INCOMING)) {
                    if (shouldCancel() && event.getPacket() instanceof S27PacketExplosion) {
                        event.setCancelled(true);
                    }
                }
                break;
            }
        }
    }

    public void handleExplosion(Minecraft gameController, S27PacketExplosion packet) {
        if (!shouldCancel()) {
            if (Math.random() <= chance.get() / 100) {
                gameController.player.motionX += packet.getMotionX() * horizontal.get() / 100;
                gameController.player.motionY += packet.getMotionY() * vertical.get() / 100;
                gameController.player.motionZ += packet.getMotionZ() * horizontal.get() / 100;
            } else {
                gameController.player.motionX += packet.getMotionX();
                gameController.player.motionY += packet.getMotionY();
                gameController.player.motionZ += packet.getMotionZ();
            }
        }
    }

    @EventTarget
    public void onUpdate(TickUpdateEvent event) {
        //velocity_mode.get()
        switch (velocity_mode.get()){
            case "Simple":{
                setSuffix(horizontal.get() + ".0%" + " " + vertical.get() + ".0%");
                break;
            } case "Karhu": {
                setSuffix("Karhu");
                break;
            }
        }

    }

    @Override
    public void onEnable() {
        switch (velocity_mode.get()) {
            case "Simple":{
                setSuffix(horizontal.get() + ".0%" + " " + vertical.get() + ".0%");
                break;
            } case "Karhu": {
                setSuffix("Karhu");
                break;
            }
        }
        super.onEnable();
    }
}
