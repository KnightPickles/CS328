package edu.cs328;

import java.util.ArrayList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class HW4 extends ApplicationAdapter {
	public static final float PPM = 8f; // Pixels per meter. Might be better at 8. Used for sprite coordinates and scaling.
	public static final int SCALE = 3;
	public int SCALED_W; // Gdx width / Scale
	public int SCALED_H;
	public static HW4 _instance;
	
	Batch batch;
	TextureAtlas atlas;
	OrthographicCamera camera;
	OrthographicCamera cameraGUI;
	Viewport viewport;
	BitmapFont font;
	GlyphLayout layout;
	World world;
	Map map;
	EntityManager entityManager;
	SelectionManager selectionManager;
	ShapeRenderer shapeRenderer;

	Box2DDebugRenderer debugRenderer;
	Matrix4 debugMatrix;

	@Override
	public void create() {
		_instance = this;
		
		SCALED_W = Gdx.graphics.getWidth() / SCALE;
		SCALED_H = Gdx.graphics.getHeight() / SCALE;

		// Box2D
		world = new World(new Vector2(0, 0), true);
		world.setContactListener(new GameCollision());
		debugRenderer = new Box2DDebugRenderer();
		
		// Graphics		
		camera = new OrthographicCamera(SCALED_W, SCALED_H);
		viewport = new FitViewport(SCALED_W, SCALED_H, camera);
		batch = new SpriteBatch();
		atlas = new TextureAtlas("rts.atlas");
		map = new Map(100, 100, atlas, camera);
		entityManager = new EntityManager(atlas, world);
		shapeRenderer = new ShapeRenderer();
		
		selectionManager = new SelectionManager();
		MyInputProcessor inputProcessor = new MyInputProcessor();
		Gdx.input.setInputProcessor(inputProcessor);
		
		// Font
		layout = new GlyphLayout();
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(new FileHandle("font.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = 4;
		font = generator.generateFont(parameter);
		generator.dispose();
	}

	@Override
	public void render() {
		// Update
		entityManager.update();
		world.step(1f/60f, 6, 2);
		cameraUpdate();

		// Draw
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

		//debugMatrix = batch.getProjectionMatrix().cpy().scale(PPM, PPM, 0);
		//debugRenderer.render(world, debugMatrix);

		batch.setTransformMatrix(camera.view);
		batch.setProjectionMatrix(camera.projection);
		batch.begin();
		map.draw(batch);
		entityManager.draw(batch);
		batch.end();
		
		if (renderLines.size > 0) {
			shapeRenderer.setProjectionMatrix(camera.projection);
			shapeRenderer.setTransformMatrix(camera.view);
			shapeRenderer.begin(ShapeType.Line);
			for (int i = 0; i < renderLines.size; i+=2) {
				shapeRenderer.line(renderLines.get(i), renderLines.get(i+1));
			}
			shapeRenderer.end();
		}
		
		debugRenderer.render(world, camera.combined);
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
		camera.update();

		SCALED_W = Gdx.graphics.getWidth() / SCALE;
		SCALED_H = Gdx.graphics.getHeight() / SCALE;
	}


	public void cameraUpdate() {
		// pixel coords starting 0,0 in top left corner
		int mx = Gdx.input.getX() / SCALE;
		int my = Gdx.input.getY() / SCALE;
		int threshold = SCALED_H / 50; // for when mouse is within ~x% of screen bounds
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

		// Camera bounds set to world size in Map
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
	
	public OrthographicCamera GetCamera() {
		return camera;
	}

	Array<Vector2> renderLines = new Array<Vector2>();
	public void renderSelectionBox(Vector2 start, Vector2 end) {
		
		renderLines.clear();
		renderLines.add(new Vector2(start.x, start.y));
		renderLines.add(new Vector2(end.x, start.y));
		
		renderLines.add(new Vector2(start.x, start.y));
		renderLines.add(new Vector2(start.x, end.y));
		
		renderLines.add(new Vector2(start.x, end.y));
		renderLines.add(new Vector2(end.x, end.y));
		
		renderLines.add(new Vector2(end.x, start.y));
		renderLines.add(new Vector2(end.x, end.y));
	}
	
	public void stopRenderSelectionBox() {
		renderLines.clear();
	}
}