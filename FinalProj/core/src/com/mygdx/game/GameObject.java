package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

/**
 * Created by KnightPickles on 4/15/16.
 */
public class GameObject {
    protected Body body;
    protected Sprite sprite;

    GameObject(String atlasRegion, float x, float y, boolean isStatic, boolean isSensor) {
        sprite = MainGameClass._instance.atlas.createSprite(atlasRegion);
        if(sprite == null) sprite = new Sprite();
        sprite.setPosition(x,y);
        setBody(isStatic, isSensor, 0, 0);
    }

    GameObject() {
        sprite = new Sprite();
        body = null;
    }

    public void draw() {
        MainGameClass._instance.batch.begin();
        sprite.draw(MainGameClass._instance.batch);
        MainGameClass._instance.batch.end();
    }

    public void update() {
        if(sprite != null && body != null)
            sprite.setPosition((body.getPosition().x * MainGameClass.PPM) - sprite.getWidth()/2 , (body.getPosition().y * MainGameClass.PPM) - sprite.getHeight()/2);
    }

    protected void setBody(boolean isStatic, boolean isSensor, int widthPad, int verticalPad) {
        BodyDef bodyDef = new BodyDef();
        if(isStatic)
            bodyDef.type = BodyDef.BodyType.StaticBody;
        else bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set((sprite.getX() + sprite.getWidth() / 2) / MainGameClass.PPM, (sprite.getY() + sprite.getHeight()/2) / MainGameClass.PPM);
        body = GameScreen._instance.world.createBody(bodyDef);
        body.setUserData(this);
        body.setFixedRotation(true);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox((sprite.getWidth() - widthPad) / 2 / MainGameClass.PPM * sprite.getScaleX(), (sprite.getHeight() - verticalPad) / 2 / MainGameClass.PPM * sprite.getScaleY());

        // Physics attributes
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.isSensor = isSensor;
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 3.0f;
        body.createFixture(fixtureDef);

        shape.dispose(); // only disposable object
        update();
    }
}
