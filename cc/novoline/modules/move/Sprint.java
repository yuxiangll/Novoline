package cc.novoline.modules.move;

import cc.novoline.events.EventTarget;
import cc.novoline.events.events.MotionUpdateEvent;
import cc.novoline.events.events.PacketEvent;
import cc.novoline.events.events.TickUpdateEvent;
import cc.novoline.gui.screen.setting.Manager;
import cc.novoline.gui.screen.setting.Setting;
import cc.novoline.gui.screen.setting.SettingType;
import cc.novoline.modules.AbstractModule;
import cc.novoline.modules.EnumModuleType;
import cc.novoline.modules.ModuleManager;
import cc.novoline.modules.configurations.annotation.Property;
import cc.novoline.modules.configurations.property.object.PropertyFactory;
import cc.novoline.modules.configurations.property.object.StringProperty;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.lwjgl.input.Keyboard;

import static net.minecraft.network.play.client.C0BPacketEntityAction.Action.START_SPRINTING;
import static net.minecraft.network.play.client.C0BPacketEntityAction.Action.STOP_SPRINTING;

public final class Sprint extends AbstractModule {

    private boolean sprinting;
    @Property("mode")
    private final StringProperty Sprint_mode = PropertyFactory.createString("Simple").acceptableValues("Simple", "AllDirection");

    /* constructors @on */
    public Sprint(@NonNull ModuleManager moduleManager) {
        super(moduleManager, "Sprint", "Sprint", Keyboard.KEY_NONE, EnumModuleType.MOVEMENT);
        Manager.put(new Setting("SPRINT_MODE", "Sprint mode", SettingType.COMBOBOX, this, Sprint_mode));

    }

    @EventTarget
    public void onPacket(PacketEvent event) {
        switch (Sprint_mode.get()) {
            case "AllDirection": {
                if (event.getState().equals(PacketEvent.State.OUTGOING)) {
                    if (event.getPacket() instanceof C0BPacketEntityAction) {
                        C0BPacketEntityAction packet = (C0BPacketEntityAction) event.getPacket();

                        if (packet.getAction().name().toLowerCase().contains("sprint")) {
                            if (mc.player.onGround) {
                                if (packet.getAction().equals(START_SPRINTING)) {
                                    sprinting = true;
                                } else if (packet.getAction().equals(STOP_SPRINTING)) {
                                    sprinting = false;
                                }
                            }

                            event.setCancelled(true);
                        }
                    }
                }
                break;
            }
        }
    }

    /* events */
    @EventTarget
    public void onMotion(MotionUpdateEvent event) {
        switch (Sprint_mode.get()) {
            case "AllDirection": {
                if (event.getState().equals(MotionUpdateEvent.State.PRE)) {
                    mc.player.setSprinting(mc.player.isMoving());

                    if (mc.player.onGround && !isEnabled(Scaffold.class)) {
                        if (mc.player.isSprinting() != sprinting) {
                            C0BPacketEntityAction packetEntity = new C0BPacketEntityAction(mc.player, mc.player.isSprinting() ? START_SPRINTING : STOP_SPRINTING);
                            sendPacketNoEvent(packetEntity);
                            sprinting = mc.player.isSprinting();
                        }
                    }
                }
                break;
            }
        }
    }
    @EventTarget
    public void onTick(TickUpdateEvent event){
        switch (Sprint_mode.get()){
            case "Simple":{
                if (mc.player != null && mc.world != null && mc.inGameHasFocus) {
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
                }
                break;
            }
        }
    }
}
