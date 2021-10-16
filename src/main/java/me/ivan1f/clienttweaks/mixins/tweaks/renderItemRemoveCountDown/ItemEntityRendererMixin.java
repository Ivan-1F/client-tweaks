package me.ivan1f.clienttweaks.mixins.tweaks.renderItemRemoveCountDown;

import me.ivan1f.clienttweaks.categories.Tweaks;
import me.ivan1f.clienttweaks.utils.WorldUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntityRenderer.class)
public abstract class ItemEntityRendererMixin extends EntityRenderer<ItemEntity> {
    protected ItemEntityRendererMixin(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void renderItemRemoveCountDown(ItemEntity itemEntity, float yaw, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, CallbackInfo ci) {
        if (!Tweaks.renderItemRemoveCountDown) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        World world = itemEntity.getEntityWorld();
        if (world == client.world) {
            world = WorldUtils.getBestWorld(client);
            if (world != null && world != client.world) {
                Entity entity = world.getEntityById(itemEntity.getEntityId());
                if (entity instanceof ItemEntity) {
                    itemEntity = (ItemEntity) entity;
                }
            }
        }

        if (world == null) {
            return;
        }

        int cd = 6000 - itemEntity.getAge();
        Text countDown = new LiteralText(String.format("%d gt | %d s", cd, cd / 20));
        if (cd <= 600) {
            countDown.formatted(Formatting.RED);    // 30s
        } else if (cd < 1200) {
            countDown.formatted(Formatting.YELLOW); // 1 min
        } else if (cd < 3000) {
            countDown.formatted(Formatting.DARK_GREEN); // 2.5 min
        } else {
            countDown.formatted(Formatting.GREEN); // 5 min
        }

        float f = itemEntity.getHeight() + 0.5f;
        matrixStack.push();
        matrixStack.translate(0, f, 0);
        matrixStack.multiply(this.renderManager.getRotation());
        matrixStack.scale(-0.018F, -0.018F, 0.018F);
        matrixStack.translate(0, 0, -33);
        Matrix4f lv = matrixStack.peek().getModel();
        float g = client.options.getTextBackgroundOpacity(0.25F);
        int k = (int) (g * 255.0F) << 24;
        TextRenderer lv2 = this.getFontRenderer();
        float h = (float) (-lv2.getStringWidth(countDown.asFormattedString()) / 2);
        lv2.draw(countDown.asFormattedString(), h, 0, 553648127, false, lv, vertexConsumerProvider, true, k, light);
        lv2.draw(countDown.asFormattedString(), h, 0, -1, false, lv, vertexConsumerProvider, true, 0, light);
        matrixStack.pop();
    }
}
