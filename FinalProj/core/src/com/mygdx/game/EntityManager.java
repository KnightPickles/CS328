package com.mygdx.game;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.World;

public class EntityManager {

	public static EntityManager _instance;


    Ghost ghost = new Ghost(Ghost.Size.X11, Ghost.Color.BLUE);

    EntityManager() {
    	if (_instance != null) System.out.println("Creating multiple entity managers");
        _instance = this;
    }
	
    public void update(float delta) {

    }
    
    public void draw() {
        //ghost.update();
        //ghost.draw();
    }
}
