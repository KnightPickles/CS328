package edu.cs328;

import java.util.ArrayList;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.sun.org.glassfish.external.statistics.Stats;

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
    public ComponentMapper<Box2dComponent> boxc = ComponentMapper.getFor(Box2dComponent.class);
    public ComponentMapper<GhostComponent> gc = ComponentMapper.getFor(GhostComponent.class);
    public ComponentMapper<BuildingComponent> bc = ComponentMapper.getFor(BuildingComponent.class);
    
    EntityManager(TextureAtlas atlas, World world) {
    	if (_instance != null) System.out.println("Creating multiple entity managers");
        _instance = this;
    	this.atlas = atlas;
    	this.world = world;
    	for (int i = 0; i < 5; i++) {
    		UnitStats stats = new UnitStats(true, 15f, 6f, 1f, 4, 24);
    		createGhost(stats, true, new Vector2(i*10, 4), "greenghost5", true);
    	}
    
    	UnitStats stats = new UnitStats(false, 15f, 6f, 1f, 4, 24);
    	createGhost(stats, false, new Vector2(30, 50), "redghost5", false);
    }
    
    //TODO create diff functions for create unit/buildings with/for different constructors in components
    
    public void createGhost(UnitStats stats, boolean pc, Vector2 spawnPosition, String spriteName, boolean friendly) {
    	Entity e = new Entity();
    	Box2dComponent b2dc = new Box2dComponent(pc, e, atlas.createSprite(spriteName), spawnPosition, world);
    	e.add(b2dc);
    	SelectableComponent sc = new SelectableComponent(friendly);
    	e.add(sc);
    	GhostComponent uc = new GhostComponent(b2dc, stats, e);
    	e.add(uc);
    	engine.addEntity(e);
    	entities = engine.getEntitiesFor(Family.all(Box2dComponent.class).get());
    }
    
    public void update() {
        for(Entity e : engine.getEntitiesFor(Family.one(GhostComponent.class).get())) {
        	gc.get(e).update();
        }
        for (Entity e : engine.getEntitiesFor(Family.one(BuildingComponent.class).get())) {
        	bc.get(e).update();
        }
    }

    public void draw(Batch batch) {
        for(Entity e : engine.getEntitiesFor(Family.one(GhostComponent.class).get())) {
        	gc.get(e).draw(batch);
        }
        for (Entity e : engine.getEntitiesFor(Family.one(BuildingComponent.class).get())) {
        	bc.get(e).draw(batch);
        }
    }
    
    public ImmutableArray<Entity> GetListSelectables() {
    	ImmutableArray<Entity> entities;
    	entities = engine.getEntitiesFor(Family.all(SelectableComponent.class).get());
    	return entities;
    }
    
    public void KillUnit(Entity e) {
    	engine.removeEntity(e);
    }
}


