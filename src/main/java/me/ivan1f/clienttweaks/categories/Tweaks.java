package me.ivan1f.clienttweaks.categories;

import me.ivan1f.quickconfig.setting.Category;
import me.ivan1f.quickconfig.setting.ChangeHandler;
import me.ivan1f.quickconfig.setting.Setting;
import me.ivan1f.quickconfig.setting.WithHotkey;
import net.minecraft.client.MinecraftClient;

import java.util.function.Consumer;

@Category
@SuppressWarnings("unused")
public class Tweaks {
    @Setting(comment = true)
    @WithHotkey
    public static boolean renderEnchantmentBookTrade = false;
    @Setting(comment = true)
    @WithHotkey
    public static boolean renderVillagerJobSite = false;
    @Setting(comment = true)
    @WithHotkey
    public static boolean renderVillagerHome = false;
    @Setting(comment = true)
    @WithHotkey
    public static boolean renderItemRemoveCountDown = false;
    @Setting(comment = true)
    @WithHotkey
    public static boolean immediatelyRespawn = false;
    @ChangeHandler(of = "immediatelyRespawn")
    public static Consumer<Boolean> immediatelyRespawnChangeHandler = (value) -> {
        if (MinecraftClient.getInstance().player != null) {
            MinecraftClient.getInstance().player.setShowsDeathScreen(!value);
        }
    };
}
