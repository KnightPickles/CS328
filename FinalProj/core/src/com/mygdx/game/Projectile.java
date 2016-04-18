package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Projectile extends GameObject{
	
	GameObject target;
	int damage;
	float speed;
	
	public Projectile(String spriteName, GameObject target, int damage, float speed, Vector2 spawnPosition) {
		sprite = MainGameClass._instance.atlas.createSprite(spriteName);
		sprite.setPosition(spawnPosition.x, spawnPosition.y);
		
		this.target = target;
		this.damage = damage;
		this.speed = speed;
		
		setBody(false, true);
	}
	
	public void update() {
		if (!targetAlive()) {
			killUnit();
			return;
		}
		Vector2 tar = new Vector2(target.position.x, target.position.y);
		
		float dist = tar.dst(position);
		float timeToTarget = dist/speed;

		float x = MathUtils.lerp(position.x, tar.x, Gdx.graphics.getDeltaTime()/(timeToTarget));
		float y = MathUtils.lerp(position.y, tar.y, Gdx.graphics.getDeltaTime()/(timeToTarget));
		
		float degrees = (float) ((Math.atan2 (target.position.x - position.x, -(target.position.y - position.y))*180.0d/Math.PI)+90.0f);
		body.setTransform(new Vector2(x, y), 0);
		sprite.setRotation(degrees - 90);
		
		if (position.dst(tar) < .25f) {
			target.receiveDamage(damage);
			killUnit();
		}
		
		super.update();
	}
	
	boolean targetAlive() {		
		if ((target instanceof Ghost && !EntityManager._instance.ghosts.contains(target)) || !target.active) {
			return false;
		}
		return true;
	}
}
