package edu.cs328;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Cursor;
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
	boolean stop = false;

	GameObjectManager manager;
	Player player;
	Background background;
	SimpleGameObject meter;
	SimpleGameObject meter_fuel;
	SimpleGameObject lives;
	SimpleGameObject gameOver;

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
		atlas = new TextureAtlas("spritesheet.atlas");
		font = new BitmapFont();
		font.setColor(0, 0, 0, 1);
		b2dWorld = new World(new Vector2(0, -0.8f), true);
		b2dWorld.setContactListener(new GameCollision());

		player = new Player(atlas, b2dWorld, -200, camera.viewportHeight / 2 - 30);

		meter = new SimpleGameObject(atlas, "meter", -camera.viewportWidth / 2 + 45, -120);
		meter_fuel = new SimpleGameObject(atlas, "meter_fuel", -camera.viewportWidth / 2 + 52, -159);
		lives = new SimpleGameObject(atlas, "spaceman_jump1", -camera.viewportWidth / 2 + 20, 200);
		gameOver = new SimpleGameObject(atlas, "game_over", -80, -70);
		meter.sprite.scale(2);
		meter_fuel.sprite.scale(2);
		gameOver.sprite.scale(2);
		background = new Background(atlas, -120, -80);
		manager = new GameObjectManager();
		manager.addObject(player);
		manager.addObject(new PhysicsGameObject(atlas, "rock0", b2dWorld, 0, -20, true, false));
		manager.addObject(new PhysicsGameObject(atlas, "rock1", b2dWorld, 64, -20, true, false));
		manager.addObject(new Fuel(atlas, b2dWorld, 300, 0));
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
		background.update();

		// check player bounds
		if(player.sprite.getY() < camera.position.y - 30 - camera.viewportHeight / 2) {
			player.lives--;
			player.destroyBody();
			manager.destroy(player);
			if(player.lives > 0) {
				player = new Player(atlas, b2dWorld, camera.position.x - 200, camera.viewportHeight / 2 - 30);
				manager.addObject(player);
			}
			System.out.println(player.lives);
		}

		// Draw
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

		// Background
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

		// GUI
		batch.setProjectionMatrix(cameraBak.combined);
		batch.begin();
		drawGUI(batch);
		batch.end();
	}

	public void getInput() {
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT))
			player.setLVel(Math.max(player.body.getLinearVelocity().x, 1f), player.body.getLinearVelocity().y);
		if(Gdx.input.isKeyPressed(Input.Keys.LEFT))
			player.setLVel(Math.min(player.body.getLinearVelocity().x, -1f), player.body.getLinearVelocity().y);
		if(Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
			if (player.fuel > 0) {
				player.setLVel(player.body.getLinearVelocity().x, Math.max(player.body.getLinearVelocity().y, 1f));
			} else if(player.isGrounded){
				player.setLVel(player.body.getLinearVelocity().x, Math.max(player.body.getLinearVelocity().y, 1f));
			}
		}
	}

	public void drawGUI(Batch batch) {
		float x = meter_fuel.sprite.getX();
		float y = meter_fuel.sprite.getY();
		for(int i = 0; i < player.fuel / 199; i++) {
			meter_fuel.draw(batch);
			meter_fuel.sprite.setPosition(x, y + i * 4);
		}
		meter_fuel.sprite.setPosition(x,y);
		meter_fuel.draw(batch);
		meter.draw(batch);

		x = lives.sprite.getX();
		y = lives.sprite.getY();
		for(int i = 1; i <= player.lives; i++) {
			lives.draw(batch);
			lives.sprite.setPosition(x + i * 35, y);
		}
		lives.sprite.setPosition(x,y);

		if(player.lives <= 0) {
			gameOver.draw(batch);
			stop = true;
		}
	}
}