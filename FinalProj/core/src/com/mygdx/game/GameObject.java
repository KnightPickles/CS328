package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

/**
 * Created by KnightPickles on 4/15/16.
 */
public class GameObject {
    protected Body body;
    Fixture fixture;
    protected Sprite sprite;
    
    boolean active; //Alive or active
    public Vector2 position;
    
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
            sprite.setPosition((body.getPosition().x) - sprite.getWidth()/2 , (body.getPosition().y) - sprite.getHeight()/2);
        position = body.getPosition();
    }

    protected void setBody(boolean isStatic, boolean isSensor, int horizontalPad, int verticalPad) {
        BodyDef bodyDef = new BodyDef();
        if(isStatic)
            bodyDef.type = BodyDef.BodyType.StaticBody;
        else bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set((sprite.getX() + sprite.getWidth() / 2) / MainGameClass.PPM, (sprite.getY() + sprite.getHeight()/2) / MainGameClass.PPM);
        body = GameScreen._instance.world.createBody(bodyDef);
        body.setUserData(this);
        body.setFixedRotation(true);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox((sprite.getWidth() - horizontalPad) / 2 / sprite.getScaleX(), (sprite.getHeight() - verticalPad) / 2 / sprite.getScaleY());

        // Physics attributes
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.isSensor = isSensor;
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 3.0f;
        fixture = body.createFixture(fixtureDef);
        body.setTransform(sprite.getX() + sprite.getWidth() / 2, sprite.getY() + sprite.getHeight() / 2, body.getAngle());
        position = body.getPosition();
        
        shape.dispose(); // only disposable object
    }
    
    void killUnit() {
    	if (body != null && body.getFixtureList().size >= 1)
    		body.destroyFixture(body.getFixtureList().first());
    	EntityManager._instance.removeEntity(this);
    }
}
