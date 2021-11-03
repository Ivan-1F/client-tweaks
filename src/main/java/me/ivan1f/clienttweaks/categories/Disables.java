package me.ivan1f.clienttweaks.categories;

import me.ivan1f.quickconfig.setting.Category;
import me.ivan1f.quickconfig.setting.ChangeHandler;
import me.ivan1f.quickconfig.setting.Setting;
import me.ivan1f.quickconfig.setting.WithHotkey;
import net.minecraft.client.MinecraftClient;

import java.util.function.Consumer;

@Category
@SuppressWarnings("unused")
public class Disables {
    @Setting(comment = true)
    @WithHotkey
    public static boolean disableCommandBlockRendering = false;
    @Setting(comment = true)
    @WithHotkey
    public static boolean disableEndermanAngrySound = false;
    @ChangeHandler(of = "disableCommandBlockRendering")
    public static Consumer<Boolean> disableCommandBlockRenderingChangeHandler = (value) -> {
        if (MinecraftClient.getInstance().worldRenderer != null) {
            MinecraftClient.getInstance().worldRenderer.reload();
        }
    };
}
