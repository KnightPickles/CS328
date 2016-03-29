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

	public Entity enemyBase;
	public Entity alliedBase;
	
    Engine engine = new Engine();
    TextureAtlas atlas;
    World world;

    private ImmutableArray<Entity> entities;

    public ComponentMapper<SelectableComponent> sc = ComponentMapper.getFor(SelectableComponent.class);
    public ComponentMapper<Box2dComponent> boxc = ComponentMapper.getFor(Box2dComponent.class);
    public ComponentMapper<GhostComponent> gc = ComponentMapper.getFor(GhostComponent.class);
    public ComponentMapper<BuildingComponent> bc = ComponentMapper.getFor(BuildingComponent.class);
    public ComponentMapper<ProjectileComponent> pc = ComponentMapper.getFor(ProjectileComponent.class);
    
    EntityManager(TextureAtlas atlas, World world) {
    	if (_instance != null) System.out.println("Creating multiple entity managers");
        _instance = this;
    	this.atlas = atlas;
    	this.world = world;
    	for (int i = 0; i < 5; i++) {
    		createGhost(new UnitStats(true, 15f, 6f, 1f, 4, 24), true, new Vector2(i*10, 4), true, GhostComponent.UnitType.MeleeFighter);
    	}
		
    	createGhost(new UnitStats(true, 16f, 6f, 1f, 1, 16), true, new Vector2(60, 4), true, GhostComponent.UnitType.Worker);
    	createGhost(new UnitStats(true, 15f, 44f, 1f, 4, 24), true, new Vector2(80, 4), true, GhostComponent.UnitType.RangedFighter);
    	
    	stats = new UnitStats(false, 15f, 6f, 1f, 4, 24);
    	createGhost(new UnitStats(false, 15f, 6f, 1f, 4, 24), false, new Vector2(30, 50), false, GhostComponent.UnitType.MeleeFighter);
		createGhost(new UnitStats(false, 15f, 6f, 1f, 4, 24), false, new Vector2(50, 50), false, GhostComponent.UnitType.MeleeFighter);
		createGhost(new UnitStats(false, 15f, 6f, 1f, 4, 24), false, new Vector2(70, 50), false, GhostComponent.UnitType.MeleeFighter);

    	alliedBase = createBuilding(new UnitStats(true, 0, 0, 0, 0, 80), true, new Vector2(-50, -50), "mainbase", true, BuildingComponent.BuildingType.MainBase);
    	enemyBase = createBuilding(new UnitStats(true, 0, 0, 0, 0, 80), false, new Vector2(100, 100), "mainbase", false, BuildingComponent.BuildingType.MainBase);
    	
    	createBuilding(new UnitStats(true, 0, 0, 0, 0, 80), false, new Vector2(-50, 100), "hauntedmansion", true, BuildingComponent.BuildingType.HauntedMansion);
    }


    public Entity createBuilding(UnitStats stats, boolean pc, Vector2 spawnPosition, String spriteName, boolean friendly, BuildingComponent.BuildingType buildingType) {
    	Entity e = new Entity();
    	Box2dComponent b2dc = new Box2dComponent(pc, e, atlas.createSprite(spriteName), spawnPosition, world);
    	e.add(b2dc);
    	SelectableComponent sc = new SelectableComponent(friendly);
    	e.add(sc);
    	BuildingComponent uc = new BuildingComponent(b2dc, stats, e, buildingType, atlas);
    	e.add(uc);
    	engine.addEntity(e);
    	entities = engine.getEntitiesFor(Family.all(Box2dComponent.class).get());
    	return e;
    }
    
    public Entity createGhost(UnitStats stats, boolean pc, Vector2 spawnPosition, boolean friendly, GhostComponent.UnitType unitType) {
    	Entity e = new Entity();
    	Box2dComponent b2dc = new Box2dComponent(pc, e, spawnPosition, world, unitType);
    	e.add(b2dc);
    	SelectableComponent sc = new SelectableComponent(friendly);
    	e.add(sc);
    	GhostComponent uc = new GhostComponent(b2dc, stats, e, unitType, atlas);
    	e.add(uc);
    	engine.addEntity(e);
    	entities = engine.getEntitiesFor(Family.all(Box2dComponent.class).get());
    	return e;
    }
    
    public void createProjectile(Entity source, Entity target, int damage) {
    	Entity e = new Entity();
    	ProjectileComponent p = new ProjectileComponent(e, source, target, damage);
    	e.add(p);
    	engine.addEntity(e);
    }
    
    public void update() {
    	if (!alliedBase.getComponent(BuildingComponent.class).alive) {
    		//TODO Defeat - Massive "Defeat" with a clickable prompt "Return to Title Screen" and "Quit Game"
			HW4.win = false;
			HW4.stop = true;
    	} else if (!enemyBase.getComponent(BuildingComponent.class).alive) {
    		//TODO Victory - Same as defeat but with "Victory"
			HW4.win = HW4.stop = true;
		}
    	
        for(Entity e : engine.getEntitiesFor(Family.one(GhostComponent.class).get())) {
        	gc.get(e).update();
        }
        for (Entity e : engine.getEntitiesFor(Family.one(BuildingComponent.class).get())) {
        	bc.get(e).update();
        }
    }

    public void draw(Batch batch) {
		batch.begin();
        for(Entity e : engine.getEntitiesFor(Family.one(GhostComponent.class).get())) {
        	gc.get(e).draw(batch);
        }
        for (Entity e : engine.getEntitiesFor(Family.one(BuildingComponent.class).get())) {
        	bc.get(e).draw(batch);
        }
        for (Entity e : engine.getEntitiesFor(Family.one(ProjectileComponent.class).get())) {
        	pc.get(e).draw(batch);
        }
		batch.end();
    }
    
    public ImmutableArray<Entity> GetListSelectables() {
    	ImmutableArray<Entity> entities;
    	entities = engine.getEntitiesFor(Family.all(SelectableComponent.class).get());
    	return entities;
    }
    
    public ImmutableArray<Entity> GetListBuildings() {
    	ImmutableArray<Entity> entities;
    	entities = engine.getEntitiesFor(Family.all(BuildingComponent.class).get());
    	return entities;
    }
    
    public void KillUnit(Entity e) {
    	engine.removeEntity(e);
    }
}


