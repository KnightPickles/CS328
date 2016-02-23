package edu.cs328;

import com.badlogic.gdx.physics.box2d.*;

/**
 * Created by KnightPickles on 2/21/2016.
 */
public class GameCollision implements ContactListener {
    GameObjectManager manager;

    @Override
    public void beginContact(Contact contact) {
        Body a=contact.getFixtureA().getBody();
        Body b=contact.getFixtureB().getBody();
        if(a.getUserData() instanceof Player || b.getUserData() instanceof Player) {
            if(a.getUserData() instanceof Brick || b.getUserData() instanceof Brick) {
                Player.isGrounded();
            } else Player.notGrounded();
            if(a.getUserData() instanceof Fuel || b.getUserData() instanceof Fuel) {
                if(a.getUserData() instanceof Fuel)((Fuel)a.getUserData()).collected = true;
                if(b.getUserData() instanceof Fuel)((Fuel)b.getUserData()).collected = true;
                Player.fuel += Fuel.value;
            }
            if(a.getUserData() instanceof Rocket || b.getUserData() instanceof Rocket) {
                HW3.win = true;
            }
        }
    }

    @Override
    public void endContact(Contact contact) {
        System.out.println("EndContact");
        if(contact.getFixtureA().getBody().getUserData() instanceof Player || contact.getFixtureB().getBody().getUserData() instanceof Player) {
            Player.notGrounded();
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
