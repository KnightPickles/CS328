package com.mygdx.game;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.World;

public class EntityManager {

	public static EntityManager _instance;

    TextureAtlas atlas;
    World world;

    Engine engine;

    EntityManager(TextureAtlas atlas, World world) {
    	if (_instance != null) System.out.println("Creating multiple entity managers");
        _instance = this;
    	this.atlas = atlas;
    	this.world = world;
        //engine.addSystem(PathfindingSystem);
    }
	
    public void update(float delta) {
    	engine.update(delta);
    }
    
    public void render() {
    	
    }
}
