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
import net.minecraft.util.math.Vec3d;
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

    @Override
    // apply the brakes when the controlling player holds back while in the cart
    protected Vec3d applySlowdown(Vec3d velocity) {
        Vec3d vel = super.applySlowdown(velocity);

        if (this.getFirstPassenger() instanceof ServerPlayerEntity player && player.getPlayerInput().backward()) {
            // stop completely if going slowly. otherwise, slow down (but not as quickly as an unpowered powered rail)
            return vel.length() < 0.03 ? Vec3d.ZERO : vel.multiply(0.8);
        }

        return vel;
    }

    @Override
    public boolean canSprintAsVehicle() {
        return super.canSprintAsVehicle();
    }
}
