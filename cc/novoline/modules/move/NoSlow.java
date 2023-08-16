package cc.novoline.modules.move;

import cc.novoline.events.EventTarget;
import cc.novoline.events.events.*;
import cc.novoline.gui.screen.setting.Manager;
import cc.novoline.gui.screen.setting.Setting;
import cc.novoline.gui.screen.setting.SettingType;
import cc.novoline.modules.AbstractModule;
import cc.novoline.modules.EnumModuleType;
import cc.novoline.modules.ModuleManager;
import cc.novoline.modules.combat.KillAura;
import cc.novoline.modules.configurations.annotation.Property;
import cc.novoline.modules.configurations.property.object.BooleanProperty;
import cc.novoline.modules.configurations.property.object.PropertyFactory;
import cc.novoline.modules.configurations.property.object.StringProperty;
import cc.novoline.yuxiangll.MutilLanguageUtil;
import net.minecraft.item.*;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.checkerframework.checker.nullness.qual.NonNull;

import static cc.novoline.modules.configurations.property.object.PropertyFactory.booleanFalse;

public final class NoSlow extends AbstractModule {

    private boolean should_send_block_placement = false;
    private boolean enabled = false;



    /* properties @off */
    //@Property("vanilla")
    //private final BooleanProperty vanilla = booleanFalse();
    @Property("mode")
    private final StringProperty mode = PropertyFactory.createString("vanilla").acceptableValues("vanilla", "OldNCP", "Hypixel");


    /* constructors @on */
    public NoSlow(@NonNull ModuleManager moduleManager) {
        super(moduleManager, "NoSlow", MutilLanguageUtil.getString("不减速","No Slow"), EnumModuleType.MOVEMENT, "No slow down when using items");
        Manager.put(new Setting("NS_MODE","mode",SettingType.COMBOBOX,this,mode));
        //Manager.put(new Setting("NS_VANILLA", "Vanilla", SettingType.CHECKBOX, this, this.vanilla));
    }

    @EventTarget
    public void onTick(TickUpdateEvent event) {
        setSuffix(mode.get());
    }
    @EventTarget
    public void onPacket(PacketEvent event){
        Packet packet = event.getPacket();
        switch (mode.get().toLowerCase()){
            case "Hypixel": {
                if (packet instanceof C07PacketPlayerDigging && ((C07PacketPlayerDigging) packet).func_180762_c() == C07PacketPlayerDigging.Action.RELEASE_USE_ITEM && mc.player.getHeldItem() != null && !(mc.player.getHeldItem().getItem() instanceof ItemBow)) {
                    enabled = false;
                    event.setCancelled(true);
                    final int slot = mc.player.inventory.currentItem;
                    mc.getNetHandler().addToSendQueueNoEvent(new C09PacketHeldItemChange((slot + 1) % 8));
                    mc.getNetHandler().addToSendQueueNoEvent(new C09PacketHeldItemChange(slot));
                }
                if (packet instanceof C08PacketPlayerBlockPlacement) {
                    if (mc.gameSettings.keyBindUseItem.isKeyDown() && !enabled) {
                        if (mc.player.getHeldItem() != null && (mc.player.getHeldItem().getItem() instanceof ItemFood || (mc.player.getHeldItem().getItem() instanceof ItemPotion && !ItemPotion.isSplash(mc.player.getHeldItem().getMetadata())) || mc.player.getHeldItem().getItem() instanceof ItemBucketMilk))
                            event.setCancelled(true);
                            //ep.setCancelled(true);
                    }
                }
                break;
            }
        }


    }




    /* events */
    @EventTarget
    public void onBlock(MotionUpdateEvent event) {
        switch (mode.get().toLowerCase()){
            case "oldncp":
                if (mc.player.getHeldItem() != null && mc.player.getHeldItem().getItem() instanceof ItemSword
                        && mc.gameSettings.keyBindUseItem.isKeyDown() && !getModule(KillAura.class).shouldBlock()) {
                    if (event.getState().equals(MotionUpdateEvent.State.PRE)) {
                        sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                    } else {
                        sendPacket(new C08PacketPlayerBlockPlacement(mc.player.getHeldItem()));
                    }
                }
                break;
            case "vanilla":
                break;
            case "hypixel":
                if (event.isPre()) {
                    if (mc.player.getHeldItem() != null && mc.player.getHeldItem().getItem() instanceof ItemSword && mc.player.isBlocking()) {
                        mc.getNetHandler().addToSendQueueNoEvent(new C08PacketPlayerBlockPlacement(mc.player.getHeldItem()));
                    }
                    if (should_send_block_placement) {
                        for (int i = 1;i <= 3;i++) {
                            if (isOnGround(i)) {
                                final BlockPos pos = new BlockPos(mc.player.posX, mc.player.posY - i, mc.player.posZ);
                                MovingObjectPosition position = new MovingObjectPosition(new Vec3(((int)mc.player.posX) + 0.5, ((int)mc.player.posY) - i + 0.5, ((int)mc.player.posZ) + 0.5), EnumFacing.DOWN, pos);
                                mc.playerController.onPlayerRightClick(mc.player, mc.world, mc.player.getHeldItem(), pos, position.facing, position.hitVec);
                                break;
                            }
                        }
                        should_send_block_placement = false;
                    }
                    if (!enabled && mc.player.isUsingItem() && !(mc.player.getHeldItem().getItem() instanceof ItemSword)) {
                        enabled = true;

                        final MovingObjectPosition mousePos = mc.objectMouseOver;

                        if (mousePos.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                            mc.playerController.onPlayerRightClick(mc.player, mc.world, mc.player.getHeldItem(), mousePos.getBlockPos(), mousePos.facing, mousePos.hitVec);
                        } else {
                            event.setPitch(90);
                            //eu.setPitch(90);
                            should_send_block_placement = true;

                            return;
                        }
                    } else if (enabled) {
                        if (!mc.player.isUsingItem()) {
                            enabled = false;
                        }
                    }

                    if (mc.gameSettings.keyBindUseItem.isKeyDown() && mc.player.getHeldItem() != null && mc.player.getHeldItem().getItem() instanceof ItemBow) {
                        final int slot = mc.player.inventory.currentItem;
                        mc.getNetHandler().addToSendQueueNoEvent(new C09PacketHeldItemChange((slot + 1) % 8));
                        mc.getNetHandler().addToSendQueueNoEvent(new C09PacketHeldItemChange(slot));
                        mc.getNetHandler().addToSendQueueNoEvent(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, null, 0.0f, 0.0f, 0.0f));
                    }
                }
                break;
        }

    }

    public  boolean isOnGround(double height) {
        return !mc.world.getCollidingBoundingBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, -height, 0.0)).isEmpty();
    }

    @EventTarget
    public void onSlowDown(SlowdownEvent event) {
        event.setCancelled(true);
    }


    @Override
    public void onEnable() {
        setSuffix(mode.get());
        super.onEnable();
    }
}
