package expresscarts;

import dev.xpple.betterconfig.api.Config;

public class ExpressCartsConfig {
    @Config(comment = "Maximum speed of Express Minecarts, in blocks per second.")
    public static double maxMinecartSpeed = 16;

    @Config(comment = "Multiplier to the max minecart speed when in water.")
    public static double waterSpeedMultiplier = 0.5;

    @Config(comment = "Whether players may hold the backwards movement key to brake.")
    public static boolean brakingEnabled = true;

    @Config(comment = "Amount by which an Express Minecart's velocity is scaled each tick their passenger holds the brakes.")
    public static double brakeSlowdown = 0.8;
}
