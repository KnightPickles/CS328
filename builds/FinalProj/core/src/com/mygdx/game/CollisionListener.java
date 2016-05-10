package com.mygdx.game;

import com.badlogic.gdx.physics.box2d.*;

public class CollisionListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        Body a=contact.getFixtureA().getBody();
        Body b=contact.getFixtureB().getBody();
        //if(a.getUserData() instanceof Player || b.getUserData() instanceof Player)
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
