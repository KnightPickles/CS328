package edu.cs328;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class HW4 extends ApplicationAdapter {
	public static final int SCALE = 2;

	Batch batch;
	TextureAtlas atlas;
	OrthographicCamera camera;
	BitmapFont font;
	GlyphLayout layout;
	World world;

	//GameObjectManager manager;
	Box2DDebugRenderer debugRenderer;
	Matrix4 debugMatrix;

	public static final float PPM = 100f;

	@Override
	public void create() {
		debugRenderer = new Box2DDebugRenderer();
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch = new SpriteBatch();
		atlas = new TextureAtlas("spacemangame.atlas");

		// Font
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(new FileHandle("font.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = 20;
		font = generator.generateFont(parameter); // font size 12 pixels
		generator.dispose();

		layout = new GlyphLayout();
		world = new World(new Vector2(0, 0), true);
		world.setContactListener(new GameCollision());
	}

	@Override
	public void render() {
		// Update
		world.step(1f/60f, 6, 2);
		camera.update();
		//manager.update();

		// Draw
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

		//debugMatrix = batch.getProjectionMatrix().cpy().scale(PPM, PPM, 0);
		//debugRenderer.render(world, debugMatrix);

		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		for(int i = 0; i < 20; i++) {
			for(int j = 0; j < 20; j++)
				batch.draw(atlas.findRegion("spaceman_fly0"), i * PPM - Gdx.graphics.getWidth() / 2, j * PPM - Gdx.graphics.getHeight() / 2);
		}
		batch.end();
	}

	public void drawGUI(Batch batch) {

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