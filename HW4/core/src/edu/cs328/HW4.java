package edu.cs328;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/* To Do:
 * Add title screen graphics
 * Change GUI appearance
 *
 * Seperate buttons for buildings and units; building -> set rally, train unit melee/ranged
 * Add unit information in GUI
 * Ghost eyes
 */

public class HW4 extends Game {

	public static final float PPM = 8f; // Pixels per meter. Might be better at 8. Used for sprite coordinates and scaling.
	public static final int SCALE = 3;

	Batch batch;
	ShapeRenderer shapeRenderer;
	TextureAtlas atlas;
	BitmapFont font;
	GlyphLayout layout;

	@Override
	public void create() {
		// Graphics

		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		atlas = new TextureAtlas("rts.atlas");

		// Font
		layout = new GlyphLayout();
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(new FileHandle("font.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = 4;
		font = generator.generateFont(parameter);
		generator.dispose();

		// Init to menu screen
		setScreen(new SplashScreen(this));
	}

	@Override
	public void render() {
		super.render();
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		batch.dispose();
		shapeRenderer.dispose();
		atlas.dispose();
		font.dispose();
	}

	@Override
	public void resize(int width, int height) {

	}

	public void drawFont(String text, int x, int y, boolean center) {
		layout.setText(font, text);
		float w = layout.width;
		float h = layout.height;
		if (center) {
			font.draw(batch, text, x - w / 2, y - h / 2);
		} else font.draw(batch, text, x, y);
	}
}