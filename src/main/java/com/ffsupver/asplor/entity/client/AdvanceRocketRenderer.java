package com.ffsupver.asplor.entity.client;

import com.ffsupver.asplor.Asplor;
import com.ffsupver.asplor.entity.custom.rocket.AdvanceRocketEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class AdvanceRocketRenderer extends RoundTripRocketRenderer<AdvanceRocketEntity> {
    public static final Identifier ADVANCE_ROCKET_TEXTURE = new Identifier(Asplor.MOD_ID,"textures/entity/rocket/advance_rocket.png");
    public static final EntityModelLayer ADVANCE_ROCKET_MODEL  = new EntityModelLayer(new Identifier(Asplor.MOD_ID,"advance_rocket"),"main");
    public AdvanceRocketRenderer(EntityRendererFactory.Context context) {
        super(context, new AdvanceRocketModel(context.getPart(ADVANCE_ROCKET_MODEL)),1.3F);
    }

    @Override
    public Identifier getTexture(AdvanceRocketEntity entity) {
        return ADVANCE_ROCKET_TEXTURE;
    }
}
