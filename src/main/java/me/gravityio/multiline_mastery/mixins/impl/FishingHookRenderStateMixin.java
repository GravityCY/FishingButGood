//? if >=1.21.2 {
/*package me.gravityio.multiline_mastery.mixins.impl;
import me.gravityio.multiline_mastery.mixins.inter.MultilineRenderState;
import net.minecraft.client.renderer.entity.state.FishingHookRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(FishingHookRenderState.class)
public class FishingHookRenderStateMixin implements MultilineRenderState {
    @Unique
    private boolean shouldRender;

    @Override
    public boolean fishingButGood$getShouldRender() {
        return this.shouldRender;
    }

    @Override
    public void fishingButGood$setShouldRender(boolean render) {
        this.shouldRender = render;
    }
}
*///?}