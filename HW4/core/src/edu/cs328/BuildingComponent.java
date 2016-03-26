package edu.cs328;

import com.badlogic.ashley.core.Entity;

public class BuildingComponent extends UnitComponent {

	public BuildingComponent(Box2dComponent b2dc, UnitStats stats, Entity myEntity) {
		super(b2dc, stats, myEntity);
	}

	
}
