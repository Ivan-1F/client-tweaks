package me.ivan1f.clienttweaks.mixins.disables.disableCommandBlockRendering;

import me.ivan1f.clienttweaks.categories.Disables;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.CommandBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CommandBlock.class)
public class CommandBlockMixin {
    @Inject(method = "getRenderType", at = @At("HEAD"), cancellable = true)
    private void getRenderType(BlockState state, CallbackInfoReturnable<BlockRenderType> cir) {
        if (Disables.disableCommandBlockRendering) {
            cir.setReturnValue(BlockRenderType.INVISIBLE);
        }
    }
}
