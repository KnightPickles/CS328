package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameScreen implements Screen {

	private MainGameClass game;
	public static GameScreen _instance;
	
    OrthographicCamera camera;
    Viewport viewport;
    World world;
    Box2DDebugRenderer debugRenderer;
    Map map;
    EntityManager entityManager;

    
	public GameScreen(MainGameClass game) {
		this.game = game;
	}
	
	@Override
	public void show() {
		_instance = this;
		game.batch = new SpriteBatch();
		
        camera = new OrthographicCamera(Gdx.graphics.getWidth() / game.SCALE, Gdx.graphics.getHeight() / game.SCALE);
        viewport = new FitViewport(Gdx.graphics.getWidth() / game.SCALE, Gdx.graphics.getHeight() / game.SCALE, camera);
		
        // Box2D
        world = new World(new Vector2(0, 0), true);
        world.setContactListener(new CollisionListener());
        debugRenderer = new Box2DDebugRenderer();
        
        map = new Map("level2.png", game, game.atlas, camera);
        entityManager = new EntityManager(game.atlas, world);
        MyInputProcessor inputProcessor = new MyInputProcessor();
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        //inputMultiplexer.addProcessor(gui.stage);
        inputMultiplexer.addProcessor(inputProcessor);
        Gdx.input.setInputProcessor(inputMultiplexer);
	}

	@Override
	public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);
        game.batch.setTransformMatrix(camera.view);
        game.batch.setProjectionMatrix(camera.combined);
        game.shapeRenderer.setTransformMatrix(camera.view);
        game.shapeRenderer.setProjectionMatrix(camera.combined);
        map.draw(game.batch);
	}

	@Override
	public void resize(int width, int height) {
        viewport.update(width, height);
        camera.update();
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

}
