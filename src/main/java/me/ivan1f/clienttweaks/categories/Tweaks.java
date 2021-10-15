package me.ivan1f.clienttweaks.categories;

import me.ivan1f.quickconfig.setting.Category;
import me.ivan1f.quickconfig.setting.Setting;
import me.ivan1f.quickconfig.setting.WithHotkey;

@Category
public class Tweaks {
    @Setting
    @WithHotkey
    public static boolean renderEnchantmentBookTrade = false;
}
