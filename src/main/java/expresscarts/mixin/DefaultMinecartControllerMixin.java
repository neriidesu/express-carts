package expresscarts.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import expresscarts.ExpressMinecartEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.DefaultMinecartController;
import net.minecraft.entity.vehicle.MinecartController;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Mixin(DefaultMinecartController.class)
public abstract class DefaultMinecartControllerMixin extends MinecartController {
    @Unique
    private boolean expresscarts$isExpressMinecart = false;

    // This shouldn't actually get called
    protected DefaultMinecartControllerMixin(AbstractMinecartEntity minecart) {
        super(minecart);
    }

    @Inject(method = "Lnet/minecraft/entity/vehicle/DefaultMinecartController;<init>(Lnet/minecraft/entity/vehicle/AbstractMinecartEntity;)V", at = @At("RETURN"))
    private void setIsExpressMinecart(AbstractMinecartEntity minecart, CallbackInfo ci) {
        if (minecart instanceof ExpressMinecartEntity) {
            this.expresscarts$isExpressMinecart = true;
        }
    }

    // All these speeds are in blocks per tick, so we divide by 20.
    @Unique
    private double expresscarts$MAX_SPEED_IN_WATER = 4 / 20d;
    @Unique
    private double expresscarts$MAX_SPEED_ON_LAND = 16 / 20d;
    @Unique
    private double expresscarts$MAX_SPEED = 16 / 20d;

    @Inject(method = "Lnet/minecraft/entity/vehicle/DefaultMinecartController;limitSpeed(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;", at = @At("HEAD"), cancellable = true)
    public void limitSpeed(Vec3d velocity, CallbackInfoReturnable<Vec3d> cir) {
        if (this.expresscarts$isExpressMinecart) {
            cir.setReturnValue(new Vec3d(
                    MathHelper.clamp(velocity.x, -this.expresscarts$MAX_SPEED, this.expresscarts$MAX_SPEED), velocity.y,
                    MathHelper.clamp(velocity.z, -this.expresscarts$MAX_SPEED, this.expresscarts$MAX_SPEED)));
            return;
        }
    }

    @Inject(method = "Lnet/minecraft/entity/vehicle/DefaultMinecartController;getMaxSpeed(Lnet/minecraft/server/world/ServerWorld;)D", at = @At("HEAD"), cancellable = true)
    public void getMaxSpeed(ServerWorld world, CallbackInfoReturnable<Double> cir) {
        if (this.expresscarts$isExpressMinecart) {
            double speed = this.minecart.isTouchingWater() ? this.expresscarts$MAX_SPEED_IN_WATER
                    : this.expresscarts$MAX_SPEED_ON_LAND;
            cir.setReturnValue(speed);
            return;
        }
    }
}
