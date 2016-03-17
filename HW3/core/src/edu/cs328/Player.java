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
public class Player extends PhysicsGameObject {

    public static int lives = 3;
    public static int fuel = 10000;
    public static final int maxFuel = 10000;
    public static final int burnRate = 10;

    public static boolean isGrounded = false;
    public static boolean isStationary = false;
    int direction = 0;

    int hitboxPadding = 8;
    float torque = 0;

    float stateTime = 0;
    Animation walkAnimation;
    Animation jumpAnimation;
    Animation landAnimation;
    Animation flyAnimation;

    Player(TextureAtlas atlas, World world, float x, float y, int lives, int fuel) {
        super(atlas, "spaceman_stand0", world, x, y);
        this.lives = lives;
        this.fuel = fuel;
        sprite = atlas.createSprite("spaceman_stand0");
        setBody(false, false, hitboxPadding, 0);

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
    }

    @Override
    public void update() {
        sprite.setPosition((body.getPosition().x * HW3.PPM) - sprite.getWidth()/2 , (body.getPosition().y * HW3.PPM) - sprite.getHeight()/2);

        if(Math.signum(body.getLinearVelocity().x) == 0)
            isStationary = true;
        else isStationary = false;

        stateTime += Gdx.graphics.getDeltaTime();
        if(isGrounded && !isStationary) {
            sprite.setRegion(walkAnimation.getKeyFrame(stateTime, true));
        } else if(isGrounded && isStationary) {
            sprite.setRegion(landAnimation.getKeyFrame(stateTime, true));
        } else if(!isGrounded) {
            if(fuel <= 0) {
                sprite.setRegion(jumpAnimation.getKeyFrame(stateTime, true));
            } else sprite.setRegion(flyAnimation.getKeyFrame(stateTime, true));
            if(body.getLinearVelocity().y > 0) {
                fuel-=burnRate;
            } else {
                sprite.setRegion(jumpAnimation.getKeyFrame(stateTime, true));
            }
        }

        // Flips the animation according to their facing direction. Also flips them right when stationary.
        sprite.flip(MathUtils.round(body.getLinearVelocity().x) >= 0 ? true : false, false);

        if(fuel > maxFuel) fuel = maxFuel;
        if(fuel <= 0) fuel = 0;
    }

    public static void isGrounded() {
        if(!isGrounded) isGrounded = true;
    }

    public static void notGrounded() {
        if(isGrounded) isGrounded = false;
    }

    public void setLVel(float x, float y) {
        body.setLinearVelocity(x,y);
    }

    public void move(float x, float y) {

    }
}
