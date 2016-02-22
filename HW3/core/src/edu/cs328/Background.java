package edu.cs328;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

/**
 * Created by KnightPickles on 2/22/2016.
 */
public class Background extends SimpleGameObject {
    float stateTime = 0;
    Animation floatAnimation;

    Background(TextureAtlas atlas, float x, float y) {
        super(atlas, "space0", 0, 0);
        TextureAtlas.AtlasRegion[] frames;
        frames = new TextureAtlas.AtlasRegion[4];
        frames[0] = atlas.findRegion("space0");
        frames[1] = atlas.findRegion("space1");
        frames[2] = atlas.findRegion("space2");
        frames[3] = atlas.findRegion("space3");
        floatAnimation = new Animation(0.25f, frames);
        sprite.scale(HW3.SCALE);
        sprite.setPosition(x,y);
    }

    @Override
    public void update() {
        stateTime += Gdx.graphics.getDeltaTime();
        sprite.setRegion(floatAnimation.getKeyFrame(stateTime, true));
    }
}
