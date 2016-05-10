package com.mygdx.game;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Player.Direction;

public class Arrow extends GameObject {
	
	int damage;
	float speed;
	float maxDistance;
	Vector2 direction;
	Vector2 spawnPosition;
	Rectangle hitBox;
	List<GameObject> ghostsHit = new ArrayList<GameObject>();
	Sound bowHit;
	
	public Arrow(String spriteName, Vector2 direction, int damage, int upgradeLevel, Vector2 spawnPosition) {
		sprite = MainGameClass._instance.atlas.createSprite(spriteName);
		sprite.setPosition(spawnPosition.x, spawnPosition.y);

		this.direction = new Vector2(direction.x, direction.y);
		this.damage = damage;
		this.speed = 10 + upgradeLevel*5;
		this.maxDistance = 100 + upgradeLevel * 40;
		this.spawnPosition = new Vector2(spawnPosition.x, spawnPosition.y);
		
		if (direction.x == -1)
			sprite.setRotation(0);
		else if (direction.x == 1)
			sprite.setRotation(180);
		else if (direction.y == -1)
			sprite.setRotation(90);
		else if (direction.y == 1)
			sprite.setRotation(270);
		
		hitBox = new Rectangle();
		hitBox.setCenter(spawnPosition);
		hitBox.setSize(10, 10);
		
		bowHit = Gdx.audio.newSound(Gdx.files.internal("bowHit.mp3"));
		
		setBody(false, true);
	}
	
	@Override
	public void update() {
		if (tooFar()) {
			killUnit();
			return;
		}
		Vector2 newPos = new Vector2(position.x + direction.x, position.y + direction.y);
		body.setTransform(newPos, 0);
		updateHitBox();
		
		for (GameObject g : EntityManager._instance.ghosts) {
			if (!ghostsHit.contains(g) &&hitBox.overlaps(g.sprite.getBoundingRectangle())) {
				long i = bowHit.play();
				bowHit.setVolume(i, .3f * GameScreen.volumeModifier);
				g.receiveDamage(damage);
				ghostsHit.add(g);
			}
		}
		
		super.update();
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
