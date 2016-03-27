package edu.cs328;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Batch;

public class BuildingComponent extends UnitComponent {

	public BuildingComponent(Box2dComponent b2dc, UnitStats stats, Entity myEntity, BuildingType type) {
		super(b2dc, stats, myEntity);
		buildingType = type;
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
	}
	
	@Override
	public void update() {
		if (!alive)
			return;
		
		super.update();
	}
}
