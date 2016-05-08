package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class GoldPile extends GameObject {

	float speed = 10f;
	float timeToTarget;
	float currTravelTime;
	Vector2 spawnPosition;
	Vector2 targetLocation;
	int chestIndex;
	int gold;
	
	public GoldPile(int gold, int chestIndex, Vector2 chestPos, Vector2 pos) {
		Vector2 dest = Map._instance.getNonZackCoords(chestPos);
		dest = new Vector2(dest.x + 4, dest.y + 4);
		sprite = MainGameClass._instance.atlas.createSprite("gold");
		sprite.setPosition(pos.x, pos.y);
		
		float moveLength = chestPos.dst(pos); //How far were going
		timeToTarget = moveLength/(speed);
		currTravelTime = 0;
		
		this.spawnPosition = pos;
		this.targetLocation = dest;
		this.gold = gold;
		this.chestIndex = chestIndex;
		
		setBody(false, true, 0, 0);
	}
	
	@Override
	public void update() {
		currTravelTime += Gdx.graphics.getDeltaTime();
		float progress = currTravelTime/timeToTarget;
		float x = MathUtils.lerp(spawnPosition.x, targetLocation.x, progress);
		float y = MathUtils.lerp(spawnPosition.y, targetLocation.y, progress);
		body.setTransform(x, y, 0);
		
		if (currTravelTime > timeToTarget) {
			((Chest)EntityManager._instance.chests.get(chestIndex)).giveGold(gold);
			killUnit();
		}
		
		super.update();
	}
}
