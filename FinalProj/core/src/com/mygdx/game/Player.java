package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;

public class Player extends GameObject {

	public float moveSpeed = 20f;
	
	public Player() {
		sprite = MainGameClass._instance.atlas.createSprite("redghost5");
		
		setBody(false, false, 0, 0);
	}
	
	@Override
	public void update() {
		movement();
		
		super.update();
	}
	
	void movement() {
		Vector2 moveAdd = new Vector2(0, 0);
		if (MyInputProcessor.wIsDown) 
			moveAdd.y += 1;
		if (MyInputProcessor.aIsDown)
			moveAdd.x -= 1;
		if (MyInputProcessor.dIsDown)
			moveAdd.x += 1;
		if (MyInputProcessor.sIsDown)
			moveAdd.y -= 1;
		
		float deltaTime = Gdx.graphics.getDeltaTime();
		moveAdd.nor();
		moveAdd = new Vector2(moveAdd.x * moveSpeed * deltaTime + position.x, moveAdd.y * moveSpeed * deltaTime + position.y);
		body.setTransform(moveAdd, 0);
	}

}
