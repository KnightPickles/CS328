package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
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
    GUI gui;
    Stage stage = new Stage();
	Sound backgroundMusic;
	State state;
	Sprite defeatScreen;

	enum State {
		Pause,
		Defeat,
        Victory,
		Play
	}
    
	public GameScreen(MainGameClass game) {
		this.game = game;
		
        backgroundMusic = Gdx.audio.newSound(Gdx.files.internal("bgmusic.mp3"));
        backgroundMusic.setLooping(0, true);;
        backgroundMusic.play();
        backgroundMusic.setVolume(0, .2f);
        defeatScreen = MainGameClass._instance.atlas.createSprite("victory");
        defeatScreen.setScale(1.5f);
        defeatScreen.setPosition(0 - defeatScreen.getWidth()/2f, 0);
        state = State.Play;
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
		waveManager = new LevelManager(3, 3, LevelManager.Difficulty.NORMAL);
		buildManager = new BuildManager();
        selectionManager = new SelectionManager();
        inputProcessor = new MyInputProcessor();
        gui = new GUI(stage);

        InputMultiplexer inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(stage);
		inputMultiplexer.addProcessor(inputProcessor);
		Gdx.input.setInputProcessor(inputMultiplexer);
    }

	@Override
	public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        stage.act(Gdx.graphics.getDeltaTime());
        gui.update();

        if (state == State.Play) {
			camera.update();
			waveManager.update();
			world.step(delta, 6, 2);
			game.batch.setProjectionMatrix(camera.combined);
	        game.shapeRenderer.setProjectionMatrix(camera.combined);
	        map.draw(game.batch);
			buildManager.update(delta);
			entityManager.update(delta);
        }
        if (state == State.Pause || state == State.Defeat) {
			//camera.update();
			//waveManager.update();
			//world.step(delta, 6, 2);
			game.batch.setProjectionMatrix(camera.combined);
	        game.shapeRenderer.setProjectionMatrix(camera.combined);
	        map.draw(game.batch);
			entityManager.update(delta);
			game.batch.begin();
			defeatScreen.draw(game.batch);
			game.batch.end();
            gui.defeat();
        }        
		debugRenderer.render(world, camera.combined);
        stage.draw();
        gui.draw();
    }

	public void setDefeat() {
		backgroundMusic.stop();
		state = State.Defeat;
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
