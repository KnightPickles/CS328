package edu.cs328;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

/**
 * Created by KnightPickles on 2/21/2016.
 */
public class ExampleGameObject {

    TextureAtlas atlas;
    World world;
    Body body;
    Sprite sprite;

    ExampleGameObject(TextureAtlas atlas, World world, Vector2 pos) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        sprite = new Sprite();

        float w = Gdx.graphics.getWidth()/HW3.PPM;
        float h = Gdx.graphics.getHeight()/HW3.PPM - 50/HW3.PPM;

        bodyDef.position.set(0,0);
        EdgeShape edgeShape = new EdgeShape();
        edgeShape.set(-w/2,-h/2,w/2,-h/2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = edgeShape;
        fixtureDef.friction = 1.0f;

        body = world.createBody(bodyDef);
        body.createFixture(fixtureDef);
        body.setUserData(this);
        edgeShape.dispose();
        sprite.setPosition((body.getPosition().x * HW3.PPM) - sprite.getWidth()/2, (body.getPosition().y  * HW3.PPM) - sprite.getHeight()/2);
    }

    public void draw(Batch batch) {
        sprite.draw(batch);
    }
}
