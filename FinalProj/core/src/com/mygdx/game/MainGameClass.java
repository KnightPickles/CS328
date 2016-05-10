package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class MainGameClass extends Game {
	
	public static final float PPM = 8f; // Pixels per meter. Might be better at 8. Used for sprite coordinates and scaling.
	public static final int SCALE = 3;
	public static MainGameClass _instance;
	
	SpriteBatch batch;
	ShapeRenderer shapeRenderer;
	TextureAtlas atlas;

	GameScreen gameScreen;

	@Override
	public void create () {
		_instance = this;

		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		atlas = new TextureAtlas("towerdefense.pack");

		this.setScreen(new SplashScreen());
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose() {
		batch.dispose();
		shapeRenderer.dispose();
		atlas.dispose();
	}
}
