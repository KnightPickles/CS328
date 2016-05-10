package com.mygdx.game;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class AOERenderer {
	
	ArrayList<GameObject> targets = new ArrayList<GameObject>();
	ArrayList<Sprite> sprites = new ArrayList<Sprite>();
	String spriteName;
	float lifeDuration;
	
	public AOERenderer(String spriteName, float lifeDuration) {
		this.spriteName = spriteName;
		this.lifeDuration = lifeDuration;
	}
	
	public void addTarget(GameObject t) {
		targets.add(t);
		Sprite s = MainGameClass._instance.atlas.createSprite(spriteName);
		s.setPosition(t.position.x, t.position.y);
		sprites.add(s);
	}
	
	public void update() {
		for (int i = 0; i < sprites.size(); i++) {
			Sprite s = sprites.get(i);
			GameObject t = targets.get(i);
			s.setPosition(t.position.x - s.getWidth()/2f, t.position.y - s.getHeight()/2f);
		}
		lifeDuration -= Gdx.graphics.getDeltaTime();
	}
	
	public void draw(Batch batch) {
		batch.begin();
		for (int i = 0; i < sprites.size(); i++) {
			sprites.get(i).draw(batch);
		}
		batch.end();
	}
	
}
