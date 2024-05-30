package me.gravityio.fishingbutgood.mixins.impl;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.gravityio.fishingbutgood.Helper;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.FishingBobberEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Debug(export = true)
@Mixin(FishingBobberEntityRenderer.class)
public abstract class FishingBobberRendererMixin extends EntityRenderer<FishingBobberEntity> {
    @Unique
    private static final Identifier HOVER_TEXTURE = new Identifier("fishing_but_good", "textures/entity/fishing_bobber_hover.png");
    @Unique
    private static final RenderLayer HOVER_LAYER = RenderLayer.getEntityCutout(HOVER_TEXTURE);

    protected FishingBobberRendererMixin(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @ModifyExpressionValue(
            method = "render(Lnet/minecraft/entity/projectile/FishingBobberEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/VertexConsumerProvider;getBuffer(Lnet/minecraft/client/render/RenderLayer;)Lnet/minecraft/client/render/VertexConsumer;",
                    ordinal = 0
            )
    )
    private VertexConsumer getHoveredTexture(VertexConsumer _original, FishingBobberEntity bobber, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        var player = bobber.getPlayerOwner();
        if (player == null) return _original;
        var rodStack = Helper.getFishingRod(player);
        if (rodStack == null) return _original;
        return Helper.getLookingBobber(player, rodStack) == bobber ? vertexConsumerProvider.getBuffer(HOVER_LAYER) : _original;
    }

}
