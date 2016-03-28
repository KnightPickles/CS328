package edu.cs328;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class ProjectileComponent implements Component {

	boolean alive = true;
	
	Sprite sprite;
	Entity myEntity;
	Entity source;
	Entity target;
	int damage;
	
	public ProjectileComponent (Entity myEntity, Entity source, Entity target, int damage) {
		if (source.getComponent(GhostComponent.class).bc.playerControlled)
			sprite = HW4._instance.atlas.createSprite("blue_indicator");
		else sprite = HW4._instance.atlas.createSprite("red_indicator");
		this.source = source;
		this.target = target;
		this.myEntity = myEntity;
		this.damage = damage;
		Vector2 pos = source.getComponent(Box2dComponent.class).position;
		sprite.setPosition(pos.x - sprite.getWidth()/2, pos.y - sprite.getHeight());
		sprite.setScale(.25f, .25f);
	}
	
	public void draw(Batch batch) {
		if (!EntityManager._instance.engine.getEntities().contains(target, true)) {
			alive = false;
			EntityManager._instance.KillUnit(myEntity);
		}
		
		if (!alive)
			return;
		
		Box2dComponent bc = target.getComponent(Box2dComponent.class);
		Vector2 tar = new Vector2(bc.position.x - bc.sprite.getWidth()/2, bc.position.y - bc.sprite.getHeight()/2);
		Vector2 position = new Vector2(sprite.getX(), sprite.getY());
		
		float dist = tar.dst(position);
		float timeToTarget = dist/25f;

		float x = MathUtils.lerp(position.x, tar.x, Gdx.graphics.getDeltaTime()/(timeToTarget));
		float y = MathUtils.lerp(position.y, tar.y, Gdx.graphics.getDeltaTime()/(timeToTarget));
		
		sprite.setPosition(x, y);
		
		if (new Vector2(sprite.getX(), sprite.getY()).dst(tar) < .25f) {
			alive = false;
			EntityManager._instance.KillUnit(myEntity);
			if (EntityManager._instance.GetListBuildings().contains(target, true)) {
				EntityManager._instance.bc.get(target).receiveDamage(damage, source);
			} else {
				EntityManager._instance.gc.get(target).receiveDamage(damage, source);
			}
		}
		
		sprite.draw(batch);
	}
	
}
