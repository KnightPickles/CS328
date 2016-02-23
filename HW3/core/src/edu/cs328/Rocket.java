package edu.cs328;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by KnightPickles on 2/22/16.
 */
public class Rocket extends PhysicsGameObject {
    Animation anim;
    float stateTime = 0;

    Rocket(TextureAtlas atlas, World world, float x, float y) {
        super(atlas, "spaceship0", world, x, y, true, true);
        TextureAtlas.AtlasRegion[] frames;
        frames = new TextureAtlas.AtlasRegion[3];
        frames[0] = atlas.findRegion("spaceship0");
        frames[1] = atlas.findRegion("spaceship1");
        frames[2] = atlas.findRegion("spaceship2");
        anim = new Animation(0.25f, frames);
    }

    @Override
    public void update() {
        stateTime += Gdx.graphics.getDeltaTime();
        sprite.setRegion(anim.getKeyFrame(stateTime, true));
    }
}
