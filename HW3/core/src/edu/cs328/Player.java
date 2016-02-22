package edu.cs328;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/**
 * Created by KnightPickles on 2/21/2016.
 */
public class Player extends Actor {
    Sprite sprite;
    TextureAtlas atlas;
    Body body;
    World world;
    public static boolean isGrounded = false;
    public static boolean isStationary = false;
    int direction = 0;

    int hitboxPadding = 4;
    float torque = 0;

    float stateTime = 0;
    Animation walkAnimation;
    Animation jumpAnimation;
    Animation landAnimation;
    Animation flyAnimation;

    Player(TextureAtlas atlas, World world, Vector2 pos) {
        sprite = atlas.createSprite("spaceman_stand0");
        sprite.flip(true,false);

        TextureAtlas.AtlasRegion[] frames;

        // walk
        frames = new TextureAtlas.AtlasRegion[3];
        frames[0] = atlas.findRegion("spaceman_walk0");
        frames[1] = atlas.findRegion("spaceman_walk1");
        frames[2] = atlas.findRegion("spaceman_walk2");
        walkAnimation = new Animation(0.25f, frames);

        // jump
        frames = new TextureAtlas.AtlasRegion[3];
        frames[0] = atlas.findRegion("spaceman_stand0");
        frames[1] = atlas.findRegion("spaceman_jump0");
        frames[2] = atlas.findRegion("spaceman_jump1");
        jumpAnimation = new Animation(0.25f, frames);

        // land
        frames = new TextureAtlas.AtlasRegion[2];
        frames[0] = atlas.findRegion("spaceman_stand1");
        frames[1] = atlas.findRegion("spaceman_stand0");
        landAnimation = new Animation(0.25f, frames);

        // fly
        frames = new TextureAtlas.AtlasRegion[2];
        frames[0] = atlas.findRegion("spaceman_fly0");
        frames[1] = atlas.findRegion("spaceman_fly1");
        flyAnimation = new Animation(0.25f, frames);

        frames = null; // garbage collector

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        // Positions and stuff
        sprite.setPosition(pos.x, pos.y);
        sprite.scale(1);

        bodyDef.position.set((sprite.getX() + sprite.getWidth()/2) / HW3.PPM, (sprite.getY() + sprite.getHeight()/2) / HW3.PPM);
        body = world.createBody(bodyDef);
        body.setUserData(this);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox((sprite.getWidth() - hitboxPadding) / HW3.PPM, sprite.getHeight() / HW3.PPM);

        // Does density and other settings
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        Fixture fixture = body.createFixture(fixtureDef);
        shape.dispose(); // all that was left over
    }

    public void draw(Batch batch) {
        sprite.draw(batch);
    }

    public void update() {
        sprite.setPosition((body.getPosition().x * HW3.PPM) - sprite.getWidth()/2 , (body.getPosition().y * HW3.PPM) -sprite.getHeight()/2 );
        stateTime += Gdx.graphics.getDeltaTime();

        if(isGrounded && !isStationary) {
            sprite.setRegion(walkAnimation.getKeyFrame(stateTime, true));
            sprite.flip(Math.signum(body.getLinearVelocity().x) > 0 ? true : false, false); // flips the animation accordingly
        }

        if(isGrounded && isStationary) {
            sprite.setRegion(landAnimation.getKeyFrame(stateTime, true));
            sprite.flip(Math.signum(body.getLinearVelocity().x) > 0 ? true : false, false); // flips the animation accordingly
        }

        if(!isGrounded) {
            sprite.setRegion(flyAnimation.getKeyFrame(stateTime, true));
            sprite.flip(Math.signum(body.getLinearVelocity().x) > 0 ? true : false, false); // flips the animation accordingly
        }

        // Establish direction based off of velocity. Used for sprite animations.
        float dir = Math.signum(body.getLinearVelocity().x);
        if(dir > 0) {
            isStationary = false;
            if(direction != 0) {
                direction = 0;
                sprite.flip(true, false);
            }
        } else if(dir < 0) {
            isStationary = false;
            if(direction != 1) {
                direction = 1;
                sprite.flip(true, false);
            }
        } else isStationary = true;
    }

    public static void isGrounded() {
        if(!isGrounded) isGrounded = true;
    }

    public static void notGrounded() {
        if(isGrounded) isGrounded = false;
    }

    public void move(Vector2 pos) {
        sprite.setPosition(pos.x, pos.y);
        body.setTransform(pos.x, pos.y, 0);
    }

    public void setLVel(float x, float y) {
        body.setLinearVelocity(x,y);
    }

}
