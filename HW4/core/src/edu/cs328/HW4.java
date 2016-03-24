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
	public static final float PPM = 16f; // Pixels per meter. Might be better at 8. Used for sprite coordinates and scaling.
	public static final int SCALE = 2;
	public int SCALED_W; // Gdx width / Scale
	public int SCALED_H;

	Batch batch;
	TextureAtlas atlas;
	OrthographicCamera camera;
	OrthographicCamera cameraGUI;
	BitmapFont font;
	GlyphLayout layout;
	World world;

	float[][] map;

	Box2DDebugRenderer debugRenderer;
	Matrix4 debugMatrix;

	@Override
	public void create() {
		SCALED_W = Gdx.graphics.getWidth() / SCALE;
		SCALED_H = Gdx.graphics.getHeight() / SCALE;

		// Graphics
		debugRenderer = new Box2DDebugRenderer();
		camera = new OrthographicCamera(SCALED_W, SCALED_H);
		batch = new SpriteBatch();
		atlas = new TextureAtlas("spacemangame.atlas");

		// Font
		layout = new GlyphLayout();
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(new FileHandle("font.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = 4;
		font = generator.generateFont(parameter);
		generator.dispose();

		// Box2D
		world = new World(new Vector2(0, 0), true);
		world.setContactListener(new GameCollision());

		SimplexNoise smpn = new SimplexNoise(0);
		map = smpn.generateOctavedSimplexNoise(50, 50, 3, 1.0f, 0.015f);
	}

	@Override
	public void render() {
		// Update
		world.step(1f/60f, 6, 2);
		cameraUpdate();

		// Draw
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

		//debugMatrix = batch.getProjectionMatrix().cpy().scale(PPM, PPM, 0);
		//debugRenderer.render(world, debugMatrix);

		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		// crude testing of simplex noise
		for(int i = 0; i < map[0].length; i++) {
			for(int j = 0; j < map[1].length; j++) {
				if(map[i][j] <= 0.1) {
					batch.draw(atlas.findRegion("brick_small0"), i * PPM - SCALED_W, j * PPM - SCALED_H);
				} else if(map[i][j] > 0.1 && map[i][j] <= 0.3) {
					batch.draw(atlas.findRegion("brick_small1"), i * PPM - SCALED_W, j * PPM - SCALED_H);
				} else if(map[i][j] > 0.3) {
					batch.draw(atlas.findRegion("spaceman_fly0"), i * PPM - SCALED_W, j * PPM - SCALED_H);
				}
			}
		}
		batch.end();
	}

	public void cameraUpdate() {
		camera.update();
		// pixel coords starting 0,0 in top left corner
		int mx = Gdx.input.getX() / SCALE;
		int my = Gdx.input.getY() / SCALE;
		int threshold = SCALED_H / 10; // for when mouse is within ~x% of screen bounds
		int moveSpeed = 2;

		// Move camera if mouse is within threshold bounds of screen border.
		// First 4 if's for corner bounds. Last 4 for edges.
		if(mx < threshold && my < threshold) {
			camera.position.x -= moveSpeed; // slow it a bit
			camera.position.y += moveSpeed;
		} else if(mx < threshold && my > SCALED_H - threshold) {
			camera.position.x -= moveSpeed;
			camera.position.y -= moveSpeed;
		} else if(mx > SCALED_W - threshold && my < threshold) {
			camera.position.x += moveSpeed;
			camera.position.y += moveSpeed;
		} else if(mx > SCALED_W - threshold && my > SCALED_H - threshold) {
			camera.position.x += moveSpeed;
			camera.position.y -= moveSpeed;
		} else if(mx < threshold) {
			camera.position.x -= moveSpeed;
		} else if(mx > SCALED_W - threshold) {
			camera.position.x += moveSpeed;
		} else if(my < threshold) {
			camera.position.y += moveSpeed;
		} else if(my > SCALED_H - threshold) {
			camera.position.y -= moveSpeed;
		}
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