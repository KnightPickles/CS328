package edu.cs328;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.World;
import com.sun.xml.internal.bind.annotation.OverrideAnnotationOf;

import javax.xml.soap.Text;

/**
 * Created by KnightPickles on 2/21/2016.
 */
public class Fuel extends PhysicsGameObject {
    float stateTime = 0;
    Animation floatAnimation;
    boolean collected = false;
    static int value = 1000;

    Fuel(TextureAtlas atlas, World world, float x, float y) {
        super(atlas, "fuel0", world, x, y);
        setBody(true, true, 0, 6);
        collected = false;

        TextureAtlas.AtlasRegion[] frames;
        frames = new TextureAtlas.AtlasRegion[4];
        frames[0] = atlas.findRegion("fuel0");
        frames[1] = atlas.findRegion("fuel1");
        frames[2] = atlas.findRegion("fuel2");
        frames[3] = atlas.findRegion("fuel1");
        floatAnimation = new Animation(0.25f, frames);
    }

    @Override
    public void update() {
        stateTime += Gdx.graphics.getDeltaTime();
        sprite.setRegion(floatAnimation.getKeyFrame(stateTime, true));
    }
}
