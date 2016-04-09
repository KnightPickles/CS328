package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
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
		setScreen(new GameScreen(this));
	}

	@Override
	public void render () {
		
	}
	
	@Override
	public void dispose() {
		batch.dispose();
		shapeRenderer.dispose();
		atlas.dispose();
		//font.dispose();
	}
}
