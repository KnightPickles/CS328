package edu.cs328;

import java.util.ArrayList;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by KnightPickles on 3/26/16.
 */
public class EntityManager extends EntitySystem {
	
	public static EntityManager _instance;
	
    Engine engine = new Engine();
    TextureAtlas atlas;
    World world;
    
    private ImmutableArray<Entity> entities;

    public ComponentMapper<SelectableComponent> sc = ComponentMapper.getFor(SelectableComponent.class);
    public ComponentMapper<Box2dComponent> b2dc = ComponentMapper.getFor(Box2dComponent.class);
    
    EntityManager(TextureAtlas atlas, World world) {
    	if (_instance != null) System.out.println("Creating multiple entity managers");
    	_instance = this;
    	this.atlas = atlas;
    	this.world = world;
    	for (int i = 0; i < 5; i++) {
    		addToEngine(new Vector2(i*HW4.PPM, i*HW4.PPM), "redghost5");
    	}
    }
    
    //TODO create diff functions for create unit/buildings with/for different constructors in components
    
    public void addToEngine(Vector2 position, String spriteName) {
    	Entity e = new Entity();
    	Box2dComponent b2dc = new Box2dComponent(atlas.createSprite(spriteName), position, world, 15f);
    	e.add(b2dc);
    	SelectableComponent sc = new SelectableComponent();
    	e.add(sc);
    	engine.addEntity(e);
    	entities = engine.getEntitiesFor(Family.all(Box2dComponent.class).get());
    }
    
    public void addToEngine(String spriteName) {
    	addToEngine(Vector2.Zero, spriteName);
    }
    
    public void update() {
        for(Entity e : entities) {
        	Box2dComponent b = b2dc.get(e);
            //sc.sprite.setPosition(10, 10);
        }
    }

    public void draw(Batch batch) {
        for(Entity e : entities) {
        	Box2dComponent b = b2dc.get(e);
            if(b.sprite != null)
                b.draw(batch);

        }
    }
    
    public ImmutableArray<Entity> GetListSelectables() {
    	ImmutableArray<Entity> entities;
    	entities = engine.getEntitiesFor(Family.all(SelectableComponent.class).get());
    	return entities;
    }
}


