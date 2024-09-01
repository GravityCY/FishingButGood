package me.gravityio.multiline_mastery.mixins.impl;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import me.gravityio.multiline_mastery.ModConfig;
import me.gravityio.multiline_mastery.MultilineMasteryMod;
import me.gravityio.multiline_mastery.versioned.Common;
import me.gravityio.multiline_mastery.helper.ModHelper;
import me.gravityio.multiline_mastery.mixins.inter.ModPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.FishingHookRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.FishingHook;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Debug(export = true)
@Mixin(FishingHookRenderer.class)
public abstract class FishingBobberRendererMixin extends EntityRenderer<FishingHook> {
    @Unique
    private static final ResourceLocation HOVER_TEXTURE = Common.parse(MultilineMasteryMod.MOD_ID, "textures/entity/fishing_bobber_hover.png");
    @Unique
    private static final RenderType HOVER_LAYER = RenderType.entityCutout(HOVER_TEXTURE);

    protected FishingBobberRendererMixin(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @ModifyExpressionValue(
            method = "render(Lnet/minecraft/world/entity/projectile/FishingHook;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/MultiBufferSource;getBuffer(Lnet/minecraft/client/renderer/RenderType;)Lcom/mojang/blaze3d/vertex/VertexConsumer;",
                    ordinal = 0
            )
    )
    private VertexConsumer getHoveredTexture(VertexConsumer _original, FishingHook bobber, float f, float g, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i) {
        if (!ModConfig.HANDLER.instance().highlight) return _original;

        var player = (ModPlayer) bobber.getPlayerOwner();
        if (player == null) return _original;
        var rodStack = ModHelper.getFishingRod(player);
        if (rodStack == null) return _original;

        if (ModHelper.getLookingBobber(player, rodStack) == bobber) {
            return vertexConsumerProvider.getBuffer(HOVER_LAYER);
        }
        return _original;
    }

}
