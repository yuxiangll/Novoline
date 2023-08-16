package cc.novoline.events.events;

import cc.novoline.events.events.callables.CancellableEvent;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

/**
 * @author yuxiangll
 * @package cc.novoline.events.events
 * don't mind
 * @date 2023/8/16 16:19
 */
@Getter
@Setter
public class BlockAABBEvent extends CancellableEvent {

    private final World world;
    private final Block block;
    private final BlockPos blockPos;
    private AxisAlignedBB boundingBox;
    private final AxisAlignedBB maskBoundingBox;

    public BlockAABBEvent(World world,Block block,BlockPos blockPos,AxisAlignedBB axisalignedbb,AxisAlignedBB mask) {
        this.world = world;
        this.block = block;
        this.blockPos = blockPos;
        this.boundingBox = axisalignedbb;
        this.maskBoundingBox = mask;
    }


}
