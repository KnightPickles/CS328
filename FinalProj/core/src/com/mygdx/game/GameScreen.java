package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameScreen implements Screen {
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
	Sound backgroundMusic;
	State state;
    GUI gui;

    Stage stage = new Stage();
	
	public static float volumeModifier = 1; //From 0 to 1 multiplied to each volume element
	
	enum State {
		Pause,
		Defeat,
        Victory,
		Play
	}
    
	public GameScreen() {
        backgroundMusic = Gdx.audio.newSound(Gdx.files.internal("bgmusic.mp3"));
        
        long i = backgroundMusic.play();
        backgroundMusic.setLooping(i, true);;
        backgroundMusic.setVolume(i, .1f * GameScreen.volumeModifier);
        state = State.Play;
	}
	
	@Override
	public void show() {
		_instance = this;
		MainGameClass._instance.batch = new SpriteBatch();
		
        camera = new OrthographicCamera(Gdx.graphics.getWidth() / MainGameClass._instance.SCALE, Gdx.graphics.getHeight() / MainGameClass._instance.SCALE);
        viewport = new FitViewport(Gdx.graphics.getWidth() / MainGameClass._instance.SCALE, Gdx.graphics.getHeight() / MainGameClass._instance.SCALE, camera);	
        
        // Box2D
        world = new World(new Vector2(0, 0), true);
        world.setContactListener(new CollisionListener());
        debugRenderer = new Box2DDebugRenderer();
        
        map = new Map();
        entityManager = new EntityManager();
		waveManager = new LevelManager(2, 1, LevelManager.Difficulty.NORMAL);
		buildManager = new BuildManager();
        selectionManager = new SelectionManager();
        inputProcessor = new MyInputProcessor();
        gui = new GUI(stage);


        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(gui.stage);
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
            waveManager.playCondition();
			camera.update();
			waveManager.update();
			world.step(delta, 6, 2);
			MainGameClass._instance.batch.setProjectionMatrix(camera.combined);
	        MainGameClass._instance.shapeRenderer.setProjectionMatrix(camera.combined);
	        map.draw(MainGameClass._instance.batch);
			buildManager.update(delta);
			entityManager.update(delta);
        }
        if (state == State.Pause || state == State.Defeat || state == State.Victory) {
			//camera.update();
			//waveManager.update();
			//world.step(delta, 6, 2);
			MainGameClass._instance.batch.setProjectionMatrix(camera.combined);
	        MainGameClass._instance.shapeRenderer.setProjectionMatrix(camera.combined);
	        map.draw(MainGameClass._instance.batch);
			entityManager.draw();

			if(state == State.Defeat || state == State.Victory) {
				gui.endCondition();
                entityManager.endCondition();
                waveManager.endCondition();
			}
        }
        stage.draw();
        gui.draw();
		//debugRenderer.render(world, camera.combined);
	}

	public void setDefeat() {
		backgroundMusic.stop();
		state = State.Defeat;
	}

    public void reset() {
        long i = backgroundMusic.play();
        backgroundMusic.setLooping(i, true);;
        backgroundMusic.setVolume(i, .1f * GameScreen.volumeModifier);
        state = State.Play;

        buildManager = new BuildManager();
        selectionManager = new SelectionManager();
        inputProcessor = new MyInputProcessor();
        gui = new GUI(stage);
    }

    public void cleanup() {
        backgroundMusic.stop();
        stage.clear();
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
