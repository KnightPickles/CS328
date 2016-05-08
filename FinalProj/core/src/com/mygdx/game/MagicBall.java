package com.mygdx.game;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.Player.Direction;

public class MagicBall extends GameObject {
	
	int damage;
	float speed;
	float maxDistance;
	Vector2 spawnPosition;
	Vector2 targetLocation;
	Rectangle hitBox;
	float timeToTarget;
	float currTravelTime;
	Sound hit;
	
	static int currMines;
	
	
	State state;
	enum State {
		Projectile,
		Mine
	}
	
	public MagicBall(String spriteName, Vector2 targetLocation, int damage, int upgradeLevel, Vector2 spawnPosition) {
		sprite = MainGameClass._instance.atlas.createSprite(spriteName);
		sprite.setPosition(spawnPosition.x, spawnPosition.y);

		this.damage = damage;
		this.speed = 25f + upgradeLevel*5f;
		this.maxDistance = 60f + upgradeLevel * 40f;
		this.spawnPosition = new Vector2(spawnPosition.x, spawnPosition.y);
		this.targetLocation = new Vector2(targetLocation.x, targetLocation.y);
		
		hitBox = new Rectangle();
		hitBox.setCenter(spawnPosition);
		hitBox.setSize(8, 8);
		
		setBody(false, true);
		state = State.Projectile;
		
		float moveLength = targetLocation.dst(spawnPosition); //How far were going
		timeToTarget = moveLength/(speed);
		currTravelTime = 0;
		
		hit = Gdx.audio.newSound(Gdx.files.internal("magicBall.mp3"));
	}
	
	@Override
	public void update() {
		if (tooFar()) {
			//killUnit();
			//return;
		}

		if (state == State.Projectile) {
			projectileUpdate();
		} else if (state == State.Mine) {
			mineUpdate();
		}
		
		super.update();
	}
	
	boolean detonated = false;
	void projectileUpdate() {
		
		currTravelTime += Gdx.graphics.getDeltaTime();
		float progress = currTravelTime/timeToTarget;
		float x = MathUtils.lerp(spawnPosition.x, targetLocation.x, progress);
		float y = MathUtils.lerp(spawnPosition.y, targetLocation.y, progress);
		body.setTransform(x, y, 0);
		
		updateHitBox();
		
		for (GameObject g : EntityManager._instance.ghosts) {
			if (hitBox.overlaps(g.sprite.getBoundingRectangle())) {
				long i = hit.play();
				hit.setVolume(i, .3f);
				g.receiveDamage(damage);
				detonated = true;
			}
		}
		if (detonated)
			killUnit();
		
		if (progress >= 1f && currMines < 5) {
			state = State.Mine;
			currMines++;
		} else if (progress >= 1) {
			killUnit();
		}
	}
	
	boolean detonating = false;
	float detonateTimer = .5f;
	float lifeTimer = 5f;
	void mineUpdate() {
		if (!detonating) {
			lifeTimer -= Gdx.graphics.getDeltaTime();
			if (lifeTimer <= 0) {
				detonating = true;
				detonateTimer = 0;
			}
			for (GameObject g : EntityManager._instance.ghosts) {
				if (hitBox.overlaps(g.sprite.getBoundingRectangle())) {
					detonating = true;
				}
			}
		}
		else if (detonating) {
			detonateTimer -= Gdx.graphics.getDeltaTime();
			if (detonateTimer <= 0) {
				for (GameObject g : EntityManager._instance.ghosts) {
					if (hitBox.overlaps(g.sprite.getBoundingRectangle())) {
						g.receiveDamage(damage);
					}
				}
				long i = hit.play();
				hit.setVolume(i, .3f);
				killUnit();
				currMines--;
			}				
		}
	}
	
	void updateHitBox() {
		hitBox.setCenter(new Vector2(position.x, position.y));
	}
	
	boolean tooFar() {
		if (spawnPosition.dst(position) > maxDistance) {
			return true;
		}
		return false;
	}
}
