package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
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
	TextureAtlas gui;
    
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

		gui = new TextureAtlas(Gdx.files.internal("ui/ui-blue.pack"));
		skin = new Skin(Gdx.files.internal("ui/ui-blue.json"), new TextureAtlas(Gdx.files.internal("ui/ui-blue.pack")));
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
		Table table = new Table(skin);

		table.add(new TextButton("Thing", skin)).width(50).height(50).pad(3);
		table.add(new Button(skin, "plus")).width(25).height(25).pad(3);
		table.add(new Label("number", skin)).width(50).height(50).pad(3);
		table.add(new Button(skin, "minus")).width(25).height(25).pad(3);


		win = new Window("", skin, "no-dialog");
		win.setWidth(200);
		win.setHeight(100);
		win.setMovable(true);
		win.setPosition(Gdx.graphics.getWidth() / 2 - 100, 0);
		win.add(table);
		//win.row().fill().expand();



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
