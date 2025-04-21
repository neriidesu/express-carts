package expresscarts.mixin;

import expresscarts.ExpressCarts;
import expresscarts.ExpressCartsConfig;
import expresscarts.ExpressMinecartEntity;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.MinecartBehavior;
import net.minecraft.world.entity.vehicle.NewMinecartBehavior;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(NewMinecartBehavior.class)
public abstract class NewMinecartBehaviorMixin extends MinecartBehavior {
    protected NewMinecartBehaviorMixin(AbstractMinecart minecart) {
        super(minecart);
    }

    @Inject(method = "getMaxSpeed", at = @At("HEAD"), cancellable = true)
    // If we're using this controller for an ExpressMinecartEntity, use the configured speed for that
    private void onGetMaxSpeed(CallbackInfoReturnable<Double> cir) {
        if (this.minecart instanceof ExpressMinecartEntity) {
            cir.setReturnValue(ExpressCartsConfig.maxMinecartSpeed * (this.minecart.isInWater() ? ExpressCartsConfig.waterSpeedMultiplier : 1.0) / 20.0);
        }
    }
}
