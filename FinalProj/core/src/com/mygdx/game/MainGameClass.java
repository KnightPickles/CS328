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
	
	Batch batch;
	ShapeRenderer shapeRenderer;
	TextureAtlas atlas;
	BitmapFont font;
	GlyphLayout layout;
	Sprite wtf;

	@Override
	public void create () {
		_instance = this;
		
		// Graphics
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		atlas = new TextureAtlas("td.atlas");
		
		// Font
		layout = new GlyphLayout();
		//FreeTypeFontGenerator generator = new FreeTypeFontGenerator(new FileHandle("font.ttf"));
		//FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		//parameter.size = 4;
		//font = generator.generateFont(parameter);
		//generator.dispose();
		
		// Init to menu screen
		this.setScreen(new GameScreen(this));
		wtf = atlas.createSprite("dirt");
		wtf.setPosition(0, 0);
		wtf.scale(100);
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
		//font.dispose();
	}
}
