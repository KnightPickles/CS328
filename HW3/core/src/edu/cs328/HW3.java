package edu.cs328;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class HW3 extends ApplicationAdapter {
	Batch batch;
	TextureAtlas atlas;
	OrthographicCamera camera;
	BitmapFont font;
	World b2dWorld;
	Player player;
	StaticGameObject wall;

	Box2DDebugRenderer debugRenderer;
	Matrix4 debugMatrix;

	public static final float PPM = 100f;

	@Override
	public void create() {
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch = new SpriteBatch();
		font = new BitmapFont();
		font.setColor(0, 0, 0, 1);
		b2dWorld = new World(new Vector2(0, -0.8f), true);
		b2dWorld.setContactListener(new GameCollision());

		atlas = new TextureAtlas("spritesheet.atlas");
		player = new Player(atlas, b2dWorld, new Vector2(-250,camera.viewportHeight / 2));
		wall = new StaticGameObject(atlas, b2dWorld, new Vector2(0,0));
		debugRenderer = new Box2DDebugRenderer();
		Gdx.input.setInputProcessor(new PlayerInputProcessor(player));
	}

	@Override
	public void render() {
		b2dWorld.step(1f/60f, 6, 2);
		if(player.sprite.getX() >= camera.position.x - 100) camera.position.x = player.sprite.getX() + 100;
		//if(player.sprite.getX() <= -camera.viewportWidth / 2 + 40) player.body.setTransform(-camera.viewportWidth / 2 + 40, player.sprite.getY(), 0);
		//System.out.println(- camera.viewportWidth / 2 + 40 + " " + player.sprite.getX());
		camera.update();
		player.update();

		// Draw
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);
		batch.setProjectionMatrix(camera.combined);

		debugMatrix = batch.getProjectionMatrix().cpy().scale(PPM, PPM, 0);

		batch.begin();
		player.draw(batch);
		batch.end();
		debugRenderer.render(b2dWorld, debugMatrix);
	}
}