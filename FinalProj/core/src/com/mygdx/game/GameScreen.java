package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
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
    BuildManager buildManager;
    SelectionManager selectionManager;
	LevelManager waveManager;
	MyInputProcessor inputProcessor;

	Skin skin;
	Window win;
	Stage stage;

    
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
        
        map = new Map();
        entityManager = new EntityManager();
		waveManager = new LevelManager(3, 1, LevelManager.Difficulty.NORMAL);
		buildManager = new BuildManager();
        selectionManager = new SelectionManager();
        inputProcessor = new MyInputProcessor();

		skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
		//skin = new Skin();
		stage = new Stage();

        InputMultiplexer inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(stage);
		inputMultiplexer.addProcessor(inputProcessor);
		Gdx.input.setInputProcessor(inputMultiplexer);

		guiTest();
	}

	@Override
	public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);
		stage.act(Gdx.graphics.getDeltaTime());
		camera.update();
		waveManager.update();
		world.step(delta, 6, 2);
		game.batch.setProjectionMatrix(camera.combined);
        game.shapeRenderer.setProjectionMatrix(camera.combined);
        map.draw(game.batch);
		buildManager.update(delta);
		entityManager.update(delta);
		debugRenderer.render(world, camera.combined);

		stage.draw();
	}

	void guiTest() {
		win = new Window("asdf", skin);
		win.setWidth(200);
		win.setHeight(100);
		win.setMovable(true);
		win.setPosition(Gdx.graphics.getWidth() / 2 - 100, 0);
		win.add(new TextButton("Button", skin));
		win.add(new TextButton("Button", skin));
		win.row().fill().expandX();
		win.add(new TextButton("Button", skin));

		win.add(new TextButton("Button", skin));


		stage.addActor(win);
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
