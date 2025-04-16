package expresscarts;

import java.util.List;

import eu.pb4.polymer.core.api.entity.PolymerEntity;
import expresscarts.mixin.AbstractMinecartEntityAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker.SerializedEntry;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import xyz.nucleoid.packettweaker.PacketContext;

public class ExpressMinecartEntity extends MinecartEntity implements PolymerEntity {
    public ExpressMinecartEntity(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public EntityType<?> getPolymerEntityType(PacketContext context) {
        return EntityType.MINECART;
    }

    @Override
    public void modifyRawTrackedData(List<SerializedEntry<?>> data, ServerPlayerEntity player, boolean initial) {
        data.removeIf((x) -> x.handler() == AbstractMinecartEntityAccessor.getCustomBlockId().dataType()
                || x.handler() == AbstractMinecartEntityAccessor.getCustomBlockOffset().dataType()
                || x.handler() == AbstractMinecartEntityAccessor.getCustomBlockPresent().dataType());

        // we force sending custom block data of our own rather than any the actual entity serverside might have,
        // so that our custom carts are always distinguishable to vanilla clients.
        data.add(SerializedEntry.of(AbstractMinecartEntityAccessor.getCustomBlockPresent(), true));
        data.add(SerializedEntry.of(AbstractMinecartEntityAccessor.getCustomBlockOffset(),
                this.getDefaultBlockOffset()));
        data.add(SerializedEntry.of(AbstractMinecartEntityAccessor.getCustomBlockId(),
                Block.getRawIdFromState(this.getDefaultContainedBlock())));
    }

    @Override
    public BlockState getDefaultContainedBlock() {
        return Blocks.RED_CARPET.getDefaultState();
    }

    @Override
    protected Item asItem() {
        return ModItems.EXPRESS_MINECART;
    }

    @Override
    public ItemStack getPickBlockStack() {
        return new ItemStack(ModItems.EXPRESS_MINECART);
    }

}
