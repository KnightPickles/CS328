package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class Chest extends GameObject {
	
	Vector2 pos;
	int goldLeft;
	int totalGold;
	
	public Chest(Vector2 pos, int gold) {
		sprite = MainGameClass._instance.atlas.createSprite("chest");
		sprite.setPosition(pos.x, pos.y);
		this.pos = pos;
		setBody(true, false, 0, 0);
		
		goldLeft = totalGold = gold;
	}
	
	public void takeGold(int amount) {
		goldLeft -= amount;
		float percent = (float)goldLeft / (float)totalGold;
		if (percent < .25f) {
			sprite = MainGameClass._instance.atlas.createSprite("chest_open", 3);
		} else if (percent <= .5f) {
			sprite = MainGameClass._instance.atlas.createSprite("chest_open", 2);
		} else if (percent <= .75f) {
			sprite = MainGameClass._instance.atlas.createSprite("chest_open", 1);
		} else if (percent <= 1f) {
			sprite = MainGameClass._instance.atlas.createSprite("chest_open", 0);
		} 
		sprite.setPosition(pos.x, pos.y);
	}
	
	public void giveGold(int amount) {
		goldLeft += amount;
		float percent = (float)goldLeft / (float)totalGold;
		if (percent < 1f) {
			sprite = MainGameClass._instance.atlas.createSprite("chest_open", 0);
		} else if (percent <= .75f) {
			sprite = MainGameClass._instance.atlas.createSprite("chest_open", 1);
		} else if (percent <= .5f) {
			sprite = MainGameClass._instance.atlas.createSprite("chest_open", 2);
		} else if (percent <= .25f) {
			sprite = MainGameClass._instance.atlas.createSprite("chest_open", 3);
		} 
		sprite.setPosition(pos.x, pos.y);
	}
}
