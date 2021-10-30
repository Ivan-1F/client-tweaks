package me.ivan1f.clienttweaks.mixins.disables.disableEndermanAngrySound;

import me.ivan1f.clienttweaks.categories.Disables;
import net.minecraft.entity.mob.EndermanEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EndermanEntity.class)
public class EndermanEntityMixin {
    @Inject(method = "playAngrySound", at = @At("HEAD"), cancellable = true)
    private void disableEndermanAngrySound(CallbackInfo ci) {
        if (Disables.disableEndermanAngrySound) ci.cancel();
    }
}
