package edu.cs328;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

public class UnitComponent implements Component {
	Box2dComponent bc;
	Entity myEntity;
	public boolean alive = true;
	UnitStats stats;
	Vector2 position;
	
	public UnitComponent(Box2dComponent b2dc, UnitStats stats, Entity myEntity) {
		bc = b2dc;
		this.stats = stats;
		this.myEntity = myEntity;
		position = bc.position;
	}
	
	public void update() {
		
	}
	
	public void draw(Batch batch) {
		bc.draw(batch);
		position = bc.position;
	}
	
	public void rightClickCommand(Vector2 pos, Entity tar) {
		
	}
	
	public void receiveDamage(int amount) {
		stats.health -= amount;
		if (stats.health <= 0) {
			KillUnit();
		}
	}
	
	void KillUnit() {
		alive = false;
		bc.KillUnit();
		EntityManager._instance.KillUnit(myEntity);
	}
	
}
