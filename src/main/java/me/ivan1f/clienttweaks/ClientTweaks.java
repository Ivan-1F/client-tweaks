package me.ivan1f.clienttweaks;

import com.google.common.collect.ImmutableList;
import me.ivan1f.clienttweaks.categories.Tweaks;
import me.ivan1f.quickconfig.QuickConfigExtension;
import me.ivan1f.quickconfig.extension.ExtensionManager;
import net.fabricmc.api.ModInitializer;

import java.util.List;

public class ClientTweaks implements ModInitializer, QuickConfigExtension {
    @Override
    public void onInitialize() {
        ExtensionManager.manageExtension(this);
    }

    @Override
    public List<Class<?>> getCategories() {
        return ImmutableList.of(Tweaks.class);
    }

    @Override
    public String getOpenGuiHotkey() {
        return "R + C";
    }
}
