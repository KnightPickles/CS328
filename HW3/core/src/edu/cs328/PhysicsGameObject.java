package edu.cs328;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;

/**
 * Created by KnightPickles on 2/21/16.
 */
public class PhysicsGameObject extends SimpleGameObject {
    protected World world = null;
    protected Body body = null;

    PhysicsGameObject(TextureAtlas atlas, String atlasRegionName, World world, float x, float y, boolean isStatic, boolean isSensor) {
        super(atlas, atlasRegionName, x, y);
        this.world = world;
        setBody(isStatic, isSensor, 0, 0);
    }

    PhysicsGameObject(TextureAtlas atlas, String atlasRegionName, World world, float x, float y) {
        super(atlas, atlasRegionName, x, y);
        this.world = world;
    }

    protected void setBody(boolean isStatic, boolean isSensor, int wPad, int vPad) {
        sprite.setScale(HW3.SCALE);
        sprite.setPosition(x,y);

        BodyDef bodyDef = new BodyDef();
        if(isStatic)
            bodyDef.type = BodyDef.BodyType.StaticBody;
        else bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set((sprite.getX() + sprite.getWidth() / 2) / HW3.PPM, (sprite.getY() + sprite.getHeight()/2) / HW3.PPM);
        body = world.createBody(bodyDef);
        body.setUserData(this);
        body.setFixedRotation(true);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox((sprite.getWidth() - wPad) / 2 / HW3.PPM * sprite.getScaleX(), (sprite.getHeight() - vPad) / 2 / HW3.PPM * sprite.getScaleY());

        // Physics attributes
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.isSensor = isSensor;
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 3.0f;
        body.createFixture(fixtureDef);
        shape.dispose(); // only disposable object
    }

    public void destroyBody() {
        world.destroyBody(body);
    }
}
