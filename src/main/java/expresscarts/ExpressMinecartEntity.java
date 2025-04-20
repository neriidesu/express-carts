package expresscarts;

import eu.pb4.polymer.core.api.entity.PolymerEntity;
import expresscarts.mixin.AbstractMinecartAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.List;

public class ExpressMinecartEntity extends Minecart implements PolymerEntity {
    public ExpressMinecartEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public EntityType<?> getPolymerEntityType(PacketContext context) {
        return EntityType.MINECART;
    }

    @Override
    public void modifyRawTrackedData(List<SynchedEntityData.DataValue<?>> data, ServerPlayer player, boolean initial) {
        data.removeIf((x) -> x.serializer() == AbstractMinecartAccessor.getCustomBlockId().serializer()
                || x.serializer() == AbstractMinecartAccessor.getCustomBlockOffset().serializer()
                || x.serializer() == AbstractMinecartAccessor.getCustomBlockPresent().serializer());

        // we force sending custom block data of our own rather than any the actual entity serverside might have,
        // so that our custom carts are always distinguishable to vanilla clients.
        data.add(SynchedEntityData.DataValue.create(AbstractMinecartAccessor.getCustomBlockPresent(), true));
        data.add(SynchedEntityData.DataValue.create(AbstractMinecartAccessor.getCustomBlockOffset(), this.getDefaultDisplayOffset()));
        data.add(SynchedEntityData.DataValue.create(AbstractMinecartAccessor.getCustomBlockId(), Block.getId(this.getDefaultDisplayBlockState())));
    }

    @Override
    public @NotNull BlockState getDefaultDisplayBlockState() {
        return Blocks.RED_CARPET.defaultBlockState();
    }

    @Override
    protected @NotNull Item getDropItem() {
        return ModItems.EXPRESS_MINECART;
    }


    @Override
    public @NotNull ItemStack getPickResult() {
        return new ItemStack(ModItems.EXPRESS_MINECART);
    }

    @Override
    // apply the brakes when the controlling player holds back while in the cart
    protected @NotNull Vec3 applyNaturalSlowdown(Vec3 velocity) {
        Vec3 vel = super.applyNaturalSlowdown(velocity);

        if (this.getFirstPassenger() instanceof ServerPlayer player && player.getLastClientInput().backward()) {
            // stop completely if going slowly. otherwise, slow down (but not as quickly as an unpowered powered rail)
            return vel.length() < 0.03 ? Vec3.ZERO : vel.scale(1 - ExpressCarts.BRAKE_SLOWDOWN);
        }

        return vel;
    }
}
