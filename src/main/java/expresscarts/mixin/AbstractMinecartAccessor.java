package expresscarts.mixin;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractMinecart.class)
public interface AbstractMinecartAccessor {
    @Accessor("DATA_ID_DISPLAY_BLOCK")
    static EntityDataAccessor<Integer> getCustomBlockId() {
        throw new AssertionError();
    }

    @Accessor("DATA_ID_DISPLAY_OFFSET")
    static EntityDataAccessor<Integer> getCustomBlockOffset() {
        throw new AssertionError();
    }

    @Accessor("DATA_ID_CUSTOM_DISPLAY")
    static EntityDataAccessor<Boolean> getCustomBlockPresent() {
        throw new AssertionError();
    }
}
