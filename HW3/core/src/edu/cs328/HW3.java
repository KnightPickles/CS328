package edu.cs328;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class HW3 extends ApplicationAdapter {
	public static final int SCALE = 2;

	Batch batch;
	TextureAtlas atlas;
	OrthographicCamera camera;
	OrthographicCamera cameraBak;
	BitmapFont font;
	World b2dWorld;

	GameObjectManager manager;
	Player player;
	Sprite background;

	Box2DDebugRenderer debugRenderer;
	Matrix4 debugMatrix;

	public static final float PPM = 100f;

	@Override
	public void create() {
		debugRenderer = new Box2DDebugRenderer();
		Gdx.input.setInputProcessor(new PlayerInputProcessor(player));
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cameraBak = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch = new SpriteBatch();
		atlas = new TextureAtlas("sheetatlas.atlas");
		font = new BitmapFont();
		font.setColor(0, 0, 0, 1);
		b2dWorld = new World(new Vector2(0, -0.8f), true);
		b2dWorld.setContactListener(new GameCollision());

		player = new Player(atlas, b2dWorld, -200, 400);

		background = new Sprite(new Texture("SpaceBG.gif"));
		background.setPosition(-camera.viewportWidth / 2, - camera.viewportHeight / 2);

		manager = new GameObjectManager();
		manager.addObject(player);
		manager.addObject(new PhysicsGameObject(atlas, "rock0", b2dWorld, 10, -20, true));
		manager.addObject(new PhysicsGameObject(atlas, "rock1", b2dWorld, 100, -20, true));
	}

	@Override
	public void render() {
		// Update
		getInput();

		b2dWorld.step(1f/60f, 6, 2);
		if(player.sprite.getX() >= camera.position.x - 100) camera.position.x = player.sprite.getX() + 100;

		camera.update();
		cameraBak.update();
		manager.update();

		// Draw
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

		batch.setProjectionMatrix(cameraBak.combined);
		batch.begin();
		background.draw(batch);
		batch.end();

		batch.setProjectionMatrix(camera.combined);
		debugMatrix = batch.getProjectionMatrix().cpy().scale(PPM, PPM, 0);
		debugRenderer.render(b2dWorld, debugMatrix);
		batch.begin();
		manager.draw(batch);
		batch.end();
	}

	public void getInput() {
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT))
			player.setLVel(Math.max(player.body.getLinearVelocity().x, 1f), player.body.getLinearVelocity().y);
		if(Gdx.input.isKeyPressed(Input.Keys.LEFT))
			player.setLVel(Math.min(player.body.getLinearVelocity().x, -1f), player.body.getLinearVelocity().y);
		if(Gdx.input.isKeyPressed(Input.Keys.SPACE))
			player.setLVel(player.body.getLinearVelocity().x, Math.max(player.body.getLinearVelocity().y, 1f));

	}
}