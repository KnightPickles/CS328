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
	public static final float PPM = 8f; // Pixels per meter. Might be better at 8. Used for sprite coordinates and scaling.
	public static final int SCALE = 2;
	public int SCALED_W; // Gdx width / Scale
	public int SCALED_H;
	public int worldWidth = 100;
	public int worldHeight = 100;

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
		atlas = new TextureAtlas("rts.atlas");

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
		map = smpn.generateOctavedSimplexNoise(worldWidth, worldHeight, 3, 1.0f, 0.015f);
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
					batch.draw(atlas.findRegion("aquawater"), i * PPM - SCALED_W / 2, j * PPM - SCALED_H / 2);
				} else if(map[i][j] > 0.1 && map[i][j] <= 0.4) {
					batch.draw(atlas.findRegion("dirt"), i * PPM - SCALED_W / 2, j * PPM - SCALED_H / 2);
				} else if(map[i][j] > 0.4) {
					batch.draw(atlas.findRegion("sand"), i * PPM - SCALED_W / 2, j * PPM - SCALED_H / 2);
				}
			}
		}
		batch.end();
	}

	public void cameraUpdate() {
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

		// Camera bounds set to world size
		if(camera.position.x <= 0) camera.position.x = 0;
		if(camera.position.y <= 0) camera.position.y = 0;
		if(camera.position.x + camera.viewportWidth >= worldWidth * PPM) camera.position.x = worldWidth * PPM - camera.viewportWidth;
		if(camera.position.y + camera.viewportHeight >= worldHeight * PPM) camera.position.y = worldHeight * PPM - camera.viewportHeight;

		camera.update();
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