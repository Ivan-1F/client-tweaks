package me.ivan1f.clienttweaks.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.world.World;

public class WorldUtils {
    public static World getBestWorld(MinecraftClient mc) {
        IntegratedServer server = mc.getServer();

        if (mc.world != null && server != null) {
            return server.getWorld(mc.world.dimension.getType());
        } else {
            return mc.world;
        }
    }
}
