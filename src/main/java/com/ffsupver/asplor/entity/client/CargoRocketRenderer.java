package com.ffsupver.asplor.entity.client;

import com.ffsupver.asplor.Asplor;
import com.ffsupver.asplor.entity.custom.rocket.CargoRocketEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class CargoRocketRenderer extends RoundTripRocketRenderer<CargoRocketEntity> {
    public static final Identifier CARGO_ROCKET_TEXTURE = new Identifier(Asplor.MOD_ID,"textures/entity/rocket/cargo_rocket.png");
    public static final EntityModelLayer CARGO_ROCKET_MODEL  = new EntityModelLayer(new Identifier(Asplor.MOD_ID,"cargo_rocket"),"main");

    public CargoRocketRenderer(EntityRendererFactory.Context context) {
        super(context, new CargoRocketModel(context.getPart(CARGO_ROCKET_MODEL)));
    }

    @Override
    public Identifier getTexture(CargoRocketEntity entity) {
        return CARGO_ROCKET_TEXTURE;
    }
}
