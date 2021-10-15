package me.ivan1f.clienttweaks.mixins.tweaks.renderEnchantmentBookTrade;

import me.ivan1f.clienttweaks.categories.Tweaks;
import me.ivan1f.clienttweaks.utils.WorldUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.village.TradeOffer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity> extends EntityRenderer<T> {
    protected LivingEntityRendererMixin(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void renderEnchantmentBookTrade(T livingEntity, float yaw, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, CallbackInfo ci) {
        if (!(livingEntity instanceof VillagerEntity) || !Tweaks.renderEnchantmentBookTrade) {
            return;
        }
        VillagerEntity villagerEntity = (VillagerEntity) livingEntity;
        MinecraftClient client = MinecraftClient.getInstance();
        World world = livingEntity.getEntityWorld();
        if (world == client.world) {
            world = WorldUtils.getBestWorld(client);
            if (world != null && world != client.world) {
                Entity entity = world.getEntityById(livingEntity.getEntityId());
                if (entity instanceof VillagerEntity) {
                    villagerEntity = (VillagerEntity) entity;
                }
            }
        }

        if (world == null) {
            return;
        }

        Text price = null;
        Text text = null;
        for (TradeOffer offer : villagerEntity.getOffers()) {
            ItemStack sellItem = offer.getSellItem();
            Map<Enchantment, Integer> enchantment = EnchantmentHelper.getEnchantments(sellItem);
            for (Map.Entry<Enchantment, Integer> entry : enchantment.entrySet()) {
                int level = entry.getValue();
                int cost = offer.getOriginalFirstBuyItem().getCount();
                int minCost = 2 + 3 * level;
                int maxCost = minCost + 4 + level * 10;
                if (entry.getKey().isTreasure()) {
                    minCost *= 2;
                    maxCost *= 2;
                }
                Formatting color;
                if (cost <= (maxCost - minCost) / 3 + minCost) {
                    color = Formatting.GREEN;
                } else if (cost <= (maxCost - minCost) / 3 * 2 + minCost) {
                    color = Formatting.WHITE;
                } else {
                    color = Formatting.RED;
                }
                price = new LiteralText(String.format("%d(%d-%d)", cost, minCost, maxCost)).formatted(color);

                if (level == entry.getKey().getMaximumLevel()) {
                    text = entry.getKey().getName(entry.getValue()).formatted(Formatting.GOLD);
                } else {
                    text = entry.getKey().getName(entry.getValue()).formatted(Formatting.WHITE);
                }
            }
        }
//        System.out.println(villagerEntity + " | " + villagerEntity.getOffers() + " | " + text + " | " + price);
        if (text != null && price != null) {
            float f = livingEntity.getHeight() / 8 * 7;
            matrixStack.push();
            matrixStack.translate(0, f, 0);
            matrixStack.multiply(this.renderManager.getRotation());
            matrixStack.scale(-0.018F, -0.018F, 0.018F);
            matrixStack.translate(0, 0, -33);
            Matrix4f lv = matrixStack.peek().getModel();
            float g = client.options.getTextBackgroundOpacity(0.25F);
            int k = (int) (g * 255.0F) << 24;
            TextRenderer lv2 = this.getFontRenderer();
            float h = (float) (-lv2.getStringWidth(text.asFormattedString()) / 2);
            lv2.draw(text.asFormattedString(), h, 0, 553648127, false, lv, vertexConsumerProvider, true, k, light);
            lv2.draw(text.asFormattedString(), h, 0, -1, false, lv, vertexConsumerProvider, true, 0, light);
            h = (float) (-lv2.getStringWidth(price.asFormattedString()) / 2);
            lv2.draw(price.asFormattedString(), h, 11, 553648127, false, lv, vertexConsumerProvider, true, k, light);
            lv2.draw(price.asFormattedString(), h, 11, -1, false, lv, vertexConsumerProvider, true, 0, light);
            matrixStack.pop();
        }
    }
}
