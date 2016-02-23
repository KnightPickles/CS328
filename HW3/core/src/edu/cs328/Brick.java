package edu.cs328;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by KnightPickles on 2/22/16.
 */
public class Brick extends PhysicsGameObject {

    Brick(TextureAtlas atlas, World world, float x, float y) {
        super(atlas, "rock0", world, x, y);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set((sprite.getX() + sprite.getWidth() / 2) / HW3.PPM, (sprite.getY() + 15 + sprite.getHeight()) / HW3.PPM);
        body = world.createBody(bodyDef);
        body.setUserData(this);
        body.setFixedRotation(true);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox((sprite.getWidth() - 10) / 2 / HW3.PPM * sprite.getScaleX(), (sprite.getHeight() / 100 / HW3.PPM * sprite.getScaleY()));

        // Physics attributes
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 3.0f;
        body.createFixture(fixtureDef);
        shape.dispose(); // only disposable object



        BodyDef bodyDefBox = new BodyDef();
        bodyDefBox.type = BodyDef.BodyType.StaticBody;
        bodyDefBox.position.set((sprite.getX() + sprite.getWidth() / 2) / HW3.PPM, (sprite.getY() + sprite.getHeight()/2 - 1) / HW3.PPM);
        body = world.createBody(bodyDefBox);
        body.setFixedRotation(true);

        PolygonShape shapeBox = new PolygonShape();
        shapeBox.setAsBox(sprite.getWidth() / 2 / HW3.PPM * sprite.getScaleX(), ((sprite.getHeight() - 1) / 2 / HW3.PPM * sprite.getScaleY()));

        // Physics attributes
        FixtureDef fixtureDefBox = new FixtureDef();
        fixtureDefBox.shape = shape;
        fixtureDefBox.density = 0.0f;
        fixtureDefBox.friction = 0.0f;
        body.createFixture(fixtureDefBox);
        shapeBox.dispose(); // only disposable object
    }
}
