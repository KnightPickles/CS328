package edu.cs328;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;

public class BuildingComponent extends UnitComponent {

	Vector2 rallyPoint;
	
	Sprite healthBarBackground;
	Sprite healthBarLevel;
	Sprite playerIndicator;
	
	public static int alliedUpgradeLevel = 2;
	public static int enemyUpgradeLevel = 5;

	public BuildingComponent(Box2dComponent b2dc, UnitStats stats, Entity myEntity, BuildingType type, TextureAtlas atlas) {
		super(b2dc, stats, myEntity);
		buildingType = type;
		healthBarBackground = atlas.createSprite("healthbar_structure");
		if (bc.playerControlled) {
			healthBarLevel = atlas.createSprite("health_blue");
			if(type == BuildingType.MainBase) playerIndicator = atlas.createSprite("blue_indicator");
		} else {
			healthBarLevel = atlas.createSprite("health_red");
			if(type == BuildingType.MainBase) playerIndicator = atlas.createSprite("red_indicator");
		}
		
		if (buildingType == BuildingType.MainBase)
			rallyPoint = new Vector2(position.x + bc.sprite.getWidth()/2, position.y);
		if (!bc.playerControlled)
			rallyPoint = new Vector2(125, 0);
	}
	
	public enum BuildingType {
		MainBase,
		HauntedMansion
	}
	BuildingType buildingType = BuildingType.MainBase;
	
	public void increaseUpgradeLevel() {
		if (bc.playerControlled) {
			if (GhostComponent.money >= 100) {
				GhostComponent.money -= 100;
				alliedUpgradeLevel++;
				ImmutableArray<Entity> entities = EntityManager._instance.engine.getEntitiesFor(Family.one(GhostComponent.class).get());
				for (Entity e : entities) {
					if (e.getComponent(Box2dComponent.class).playerControlled) {
						e.getComponent(GhostComponent.class).upgrade();
					}
				}
			}
		}
	}
	
	public void trainWorkerUnit() {
		if (bc.playerControlled) {
			if (GhostComponent.money >= 50) {
				Entity e = EntityManager._instance.createGhost(new UnitStats(true, 16f, 6f, 1f, 1, 16), true, position, true, GhostComponent.UnitType.Worker);
				e.getComponent(GhostComponent.class).rightClickCommand(rallyPoint, null);
				GhostComponent.money -= 50;
			}
	 	}
	}
	
	public void trainMeleeUnit() {
		if (bc.playerControlled) {
			if (GhostComponent.money >= 100) {
				
				Entity e = EntityManager._instance.createGhost(new UnitStats(true, 15f, 6f, 1f, 4, 24), true, position, true, GhostComponent.UnitType.MeleeFighter);
				e.getComponent(GhostComponent.class).rightClickCommand(rallyPoint, null);
				GhostComponent.money -= 100;
			}
		} else {
			if (GhostComponent.enemyMoney >= 100) {
				Entity e = EntityManager._instance.createGhost(new UnitStats(false, 15f, 6f, 1f, 4, 24), false, position, false, GhostComponent.UnitType.MeleeFighter);
				e.getComponent(GhostComponent.class).rightClickCommand(rallyPoint, null);
				GhostComponent.enemyMoney -= 100;
				rallyPoint.x += 10;
				if (rallyPoint.x > 160)
					rallyPoint.x = 100;
			}
	 	}
	}
	
	public void trainRangedUnit() {
		if (GhostComponent.money >= 150) {
			Entity e = EntityManager._instance.createGhost(new UnitStats(true, 15f, 16f, 1f, 4, 24), true, position, true, GhostComponent.UnitType.RangedFighter);
			e.getComponent(GhostComponent.class).rightClickCommand(rallyPoint, null);
			GhostComponent.money -= 150;
		}
	}
	
	@Override
	public void draw(Batch batch) {
		if (!alive)
			return;
		
		super.draw(batch);

		if (!bc.playerControlled && GhostComponent.enemyMoney >= 100) {
			trainMeleeUnit();
		}
		
		healthBarBackground.setPosition(bc.sprite.getX(), bc.sprite.getY() + bc.sprite.getHeight() - 2);
		healthBarBackground.draw(batch);

		if(playerIndicator != null) {
			playerIndicator.setPosition(bc.sprite.getX(), bc.sprite.getY());
			playerIndicator.draw(batch);
		}

		for(int i = 0; i < healthBarBackground.getWidth(); i++) {
			if (i * stats.maxHealth / healthBarBackground.getWidth() <= stats.health) {
				healthBarLevel.setPosition(bc.sprite.getX() + i, bc.sprite.getY() + bc.sprite.getHeight() - 2);
				healthBarLevel.draw(batch);
			}
		}
	}
	
	@Override
	public void rightClickCommand(Vector2 pos, Entity target) {
		super.rightClickCommand(pos, target);
		
		if (buildingType == BuildingType.MainBase)
			rallyPoint = pos;
	}
	
	@Override
	public void update() {
		if (!alive)
			return;
		
		super.update();
	}
}
