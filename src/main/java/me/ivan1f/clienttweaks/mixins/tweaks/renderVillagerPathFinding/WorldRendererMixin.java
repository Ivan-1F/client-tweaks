package me.ivan1f.clienttweaks.mixins.tweaks.renderVillagerPathFinding;

import com.mojang.blaze3d.platform.GlStateManager;
import me.ivan1f.clienttweaks.categories.Tweaks;
import me.ivan1f.clienttweaks.utils.WorldUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {
    @Shadow private ClientWorld world;

    @Shadow @Final private MinecraftClient client;

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE_STRING",
                    args = "ldc=weather",
                    target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V"
            )
    )
    private void render(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, CallbackInfo ci) {
        ServerWorld serverWorld = null;
        if (world == client.world) {
            serverWorld = (ServerWorld) WorldUtils.getBestWorld(client);
        }
        if (serverWorld == null) {
            return;
        }
        for (Entity entity : serverWorld.getEntitiesByType(EntityType.VILLAGER, entity -> true)) {
            VillagerEntity villager = (VillagerEntity) entity;
            BlockPos homePos = null;
            if (villager.getBrain().getOptionalMemory(MemoryModuleType.HOME).isPresent()) {
                homePos = villager.getBrain().getOptionalMemory(MemoryModuleType.HOME).get().getPos();
            }

            BlockPos jobSitePos = null;
            if (villager.getBrain().getOptionalMemory(MemoryModuleType.JOB_SITE).isPresent()) {
                jobSitePos = villager.getBrain().getOptionalMemory(MemoryModuleType.JOB_SITE).get().getPos();
            }

            GlStateManager.disableTexture();
            GlStateManager.enableBlend();

            if (jobSitePos != null && Tweaks.renderVillagerJobSite) {
                drawLine(villager.getX(), villager.getY() + 1, villager.getZ(), jobSitePos.getX() + 0.5, jobSitePos.getY() + 0.5, jobSitePos.getZ() + 0.5, new Color(0, 0, 255), 5, gameRenderer.getCamera());
                drawBoxOutlined(jobSitePos.getX(), jobSitePos.getY(), jobSitePos.getZ(), jobSitePos.getX() + 1, jobSitePos.getY() + 1, jobSitePos.getZ() + 1, new Color(0, 0, 255), 5, gameRenderer.getCamera());
            }

            if (homePos != null && Tweaks.renderVillagerHome) {
                drawLine(villager.getX(), villager.getY() + 1, villager.getZ(), homePos.getX() + 0.5, homePos.getY() + 0.5, homePos.getZ() + 0.5, new Color(255, 0, 0), 5, gameRenderer.getCamera());
                drawBoxOutlined(homePos.getX(), homePos.getY(), homePos.getZ(), homePos.getX() + 1, homePos.getY() + 1, homePos.getZ() + 1, new Color(255, 0, 0), 5, gameRenderer.getCamera());
            }
        }
    }

    private static void drawLine(double startX, double startY, double startZ, double endX, double endY, double endZ, Color color, float lineWidth, Camera camera) {
        double cameraX = camera.getPos().getX();
        double cameraY = camera.getPos().getY();
        double cameraZ = camera.getPos().getZ();

        GlStateManager.disableTexture();
        GlStateManager.enableBlend();
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(GL11.GL_LINE_STRIP, VertexFormats.POSITION_COLOR);
        GlStateManager.lineWidth(lineWidth);

        bufferBuilder.vertex(startX - cameraX, startY - cameraY, startZ - cameraZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).next();
        bufferBuilder.vertex(endX - cameraX, endY - cameraY, endZ - cameraZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).next();

        Tessellator.getInstance().draw();
        GlStateManager.lineWidth(lineWidth);
    }

    private void drawBoxOutlined(double startX, double startY, double startZ, double endX, double endY, double endZ, Color color, float lineWidth, Camera camera) {
        double cameraX = camera.getPos().getX();
        double cameraY = camera.getPos().getY();
        double cameraZ = camera.getPos().getZ();

        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(GL11.GL_LINE_STRIP, VertexFormats.POSITION_COLOR);
        GlStateManager.lineWidth(lineWidth);

        bufferBuilder.vertex(startX - cameraX, startY - cameraY, startZ - cameraZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).next();
        bufferBuilder.vertex(startX - cameraX, startY - cameraY, startZ - cameraZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).next();
        bufferBuilder.vertex(endX - cameraX, startY - cameraY, startZ - cameraZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).next();
        bufferBuilder.vertex(endX - cameraX, startY - cameraY, endZ - cameraZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).next();
        bufferBuilder.vertex(startX - cameraX, startY - cameraY, endZ - cameraZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).next();
        bufferBuilder.vertex(startX - cameraX, startY - cameraY, startZ - cameraZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).next();
        bufferBuilder.vertex(startX - cameraX, endY - cameraY, startZ - cameraZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).next();
        bufferBuilder.vertex(endX - cameraX, endY - cameraY, startZ - cameraZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).next();
        bufferBuilder.vertex(endX - cameraX, endY - cameraY, endZ - cameraZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).next();
        bufferBuilder.vertex(startX - cameraX, endY - cameraY, endZ - cameraZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).next();
        bufferBuilder.vertex(startX - cameraX, endY - cameraY, startZ - cameraZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).next();
        bufferBuilder.vertex(startX - cameraX, endY - cameraY, endZ - cameraZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).next();
        bufferBuilder.vertex(startX - cameraX, startY - cameraY, endZ - cameraZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).next();
        bufferBuilder.vertex(endX - cameraX, startY - cameraY, endZ - cameraZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).next();
        bufferBuilder.vertex(endX - cameraX, endY - cameraY, endZ - cameraZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).next();
        bufferBuilder.vertex(endX - cameraX, endY - cameraY, startZ - cameraZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).next();
        bufferBuilder.vertex(endX - cameraX, startY - cameraY, startZ - cameraZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).next();
        bufferBuilder.vertex(endX - cameraX, startY - cameraY, startZ - cameraZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).next();

        Tessellator.getInstance().draw();
        GlStateManager.lineWidth(1.0f);
    }
}
