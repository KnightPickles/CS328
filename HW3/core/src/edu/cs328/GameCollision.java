package edu.cs328;

import com.badlogic.gdx.physics.box2d.*;

/**
 * Created by KnightPickles on 2/21/2016.
 */
public class GameCollision implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        Body a=contact.getFixtureA().getBody();
        Body b=contact.getFixtureB().getBody();
        if(a.getUserData() instanceof Player || b.getUserData() instanceof Player) {
            Player.isGrounded();
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
