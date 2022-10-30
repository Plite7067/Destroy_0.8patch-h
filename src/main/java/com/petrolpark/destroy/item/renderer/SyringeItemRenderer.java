package com.petrolpark.destroy.item.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.petrolpark.destroy.Destroy;
import com.simibubi.create.foundation.item.render.CreateCustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import com.simibubi.create.foundation.utility.AnimationTickHolder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;

public class SyringeItemRenderer extends CustomRenderedItemModelRenderer<SyringeItemRenderer.SyringeModel> {

    static float MOVE_TO_CENTRE_TIME = 0.25f;
    static float PAUSE_TIME = 0.6f;
    static float STAB_TIME = 0.75f;

    @Override
    protected void render(ItemStack stack, SyringeModel model, PartialItemModelRenderer renderer, TransformType transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        LocalPlayer player = Minecraft.getInstance().player;
        float partialTicks = AnimationTickHolder.getPartialTicks();

        boolean isLeftHandAnimation = (transformType == TransformType.FIRST_PERSON_LEFT_HAND);
        boolean isFirstPersonAnimation = (isLeftHandAnimation || transformType == TransformType.FIRST_PERSON_RIGHT_HAND);

        int modifier = isLeftHandAnimation ? 1 : -1; //used to reverse direction of animation

        ms.pushPose();

        if (isFirstPersonAnimation && stack.getOrCreateTag().contains("Injecting")) {

            float time = (float) player.getUseItemRemainingTicks() - partialTicks + 1.0F;
            float progress = (stack.getUseDuration() - time) / stack.getUseDuration();

            if (!isLeftHandAnimation) ms.translate(0f, 0f, 1.6f);
            
            if (progress < MOVE_TO_CENTRE_TIME) { //gradually move to centre of screen

                ms.translate(0.2f * modifier * progress / MOVE_TO_CENTRE_TIME, 0.5f * progress / MOVE_TO_CENTRE_TIME, 0.7f * modifier * progress / MOVE_TO_CENTRE_TIME);
                ms.mulPose(Vector3f.YP.rotationDegrees(120 * modifier * progress / MOVE_TO_CENTRE_TIME));
                ms.mulPose(Vector3f.ZP.rotationDegrees(-70 * modifier * progress / MOVE_TO_CENTRE_TIME));

            } else { //hold at centre of screen

                ms.translate(0.2f * modifier, 0.5f, 0.7f * modifier);
                ms.mulPose(Vector3f.YP.rotationDegrees(120 * modifier));
                ms.mulPose(Vector3f.ZP.rotationDegrees(-70 * modifier));

            };

            if (progress > PAUSE_TIME && progress <= STAB_TIME) { //rapidly move towards arm

                float stabbingProgress = (progress - PAUSE_TIME) / (STAB_TIME - PAUSE_TIME);
                ms.translate(0f, -0.8f * stabbingProgress, 0f);

            } else if (progress > STAB_TIME) { //hold in arm

                ms.translate(0f, -0.8f, 0f);

            };
        };

        itemRenderer.render(stack, TransformType.NONE, false, ms, buffer, light, overlay, model.getOriginalModel());

        ms.popPose();
    };

    @Override
    public SyringeModel createModel(BakedModel originalModel) {
        return new SyringeModel(originalModel);
    };

    public static class SyringeModel extends CreateCustomRenderedItemModel {
        public SyringeModel(BakedModel template){
            super(template, "");
        };
    };
}
