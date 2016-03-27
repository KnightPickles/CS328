package edu.cs328;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class BuildingComponent extends UnitComponent {

	Sprite healthBarBackground;
	Sprite healthBarLevel;

	public BuildingComponent(Box2dComponent b2dc, UnitStats stats, Entity myEntity, BuildingType type, TextureAtlas atlas) {
		super(b2dc, stats, myEntity);
		buildingType = type;
		healthBarBackground = atlas.createSprite("healthbar_structure");
		if(bc.playerControlled) {
			System.out.println("e");
			healthBarLevel = atlas.createSprite("health_blue");
		} else healthBarLevel = atlas.createSprite("health_red");
	}
	
	public enum BuildingType {
		MainBase,
		HauntedMansion
	}
	BuildingType buildingType = BuildingType.MainBase;
	
	@Override
	public void draw(Batch batch) {
		if (!alive)
			return;
		
		super.draw(batch);

		healthBarBackground.setPosition(bc.sprite.getX(), bc.sprite.getY() - 2);
		healthBarBackground.draw(batch);
		healthBarLevel.draw(batch);
		for(int i = 0; i < healthBarBackground.getWidth(); i++) {
			if (i * stats.maxHealth / healthBarBackground.getWidth() <= stats.health) {
				healthBarLevel.setPosition(bc.sprite.getX() + i, bc.sprite.getY() - 2);
				healthBarLevel.draw(batch);
			}
		}
	}
	
	@Override
	public void update() {
		if (!alive)
			return;
		
		super.update();
	}
}
