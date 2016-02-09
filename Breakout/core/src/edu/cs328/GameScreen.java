package edu.cs328;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Created by KnightPickles on 1/22/16.
 */
public class GameScreen implements Screen {
    protected Game game;
    protected SpriteBatch batch;
    protected ShapeRenderer sr;
    protected OrthographicCamera gameCamera;
    protected OrthographicCamera HUDCamera;
    protected Stage stage;
    protected Skin skin;

    private RenderManager renderManager;
    public GameObjectManager goManager;

    private long lastRender;
    private long render_now;
    public int frameCount = 0;
    public int lastFPS = 0;

    private long lastStep;
    private long logic_now;
    public int stepCount = 0;
    public int lastSPS = 0;

    float dt;
    float accumulator;

    private Rectangle viewportHUD;
    private Rectangle viewportGAME;

    // Game Objects
    public static int bricks = 20;
    public static int brickW = 30;
    public static int brickH = 10;

    public static int rows = 5;
    public static int cols = 4;

    public static int balls = 3;
    public static float ballRadius = 1;
    public static float paddleWidth = 1;

    public GameScreen(Game game, boolean fixed, boolean interpolated) {
        this.game = game;
        batch = game.getSpritebatch();
        sr = game.getShapeRenderer();
        gameCamera = game.getGAMECamera();
        HUDCamera = game.getHUDCamera();

        stage = new Stage();
        stage.getViewport().setCamera(HUDCamera);
        Gdx.input.setInputProcessor(stage);
        renderManager = new RenderManager(game, this);
        goManager = new GameObjectManager(100, 100);

        Brick b;
        int id = 0;
        Color c = Color.CYAN;
        for(int i = 0; i < cols; i++) { // x
            for(int j = 0; j < rows; j++) { // y
                goManager.addSimpleGameObject(new Brick(game, id, i * 5 * brickW + 10, j * 5 * brickH + 10, brickW, brickH, c));
                System.out.println((i * 5 * brickW + 10) + " " + (j * 5 * brickH + 10));
                id++;
            }
        }


        dt = 0.0133f;	// logic updates approx. @ 75 hz
    }

    @Override
    public void render(float delta) {
        // FPS/Timestep data updating according to whatever GDX is doing.

        frameCount ++;
        render_now = System.nanoTime();

        if ((render_now - lastRender) >= 1000000000)  {
            lastFPS = frameCount;
            frameCount = 0;
            lastRender = System.nanoTime();
        }

        fixedInterpolationStep(delta);
    }

    public void fixedInterpolationStep(float delta) {
        if (delta > 0.25f) delta = 0.25f;	  // note: max frame time to avoid spiral of death
        accumulator += delta;

        while (accumulator >= dt) {
            game.getGameObjectManager().savePos(); // store interpolation for next frame
            updating(dt);
            accumulator -= dt;
            game.getGameObjectManager().interpolate(accumulator / dt); // interpolate current position

            // Throttle/manage logic steps
            stepCount++;
            logic_now = System.nanoTime();
            if ((logic_now - lastStep) >= 1000000000)  {
                lastSPS = stepCount;
                stepCount = 0;
                lastStep = System.nanoTime();
            }
        }

        rendering(delta);
    }

    public void updating(float delta) {
        goManager.update();
        //for (i = 0; i < BOXCOUNT; i++) boxes.get(i).update(delta);
        //box2dWorld.step(delta * 1f, 10, 8);
    }

    public void rendering(float delta) {
        renderManager.renderHUD();
        goManager.render(sr);
        game.updateHUDCam();
        stage.act();
        stage.draw();;
    }

    @Override
    public void resize(int width, int height) {
        viewportGAME = game.getViewportGAME();
        viewportHUD = game.getViewportHUD();

        if (viewportHUD != null) {
            stage.getViewport().setScreenBounds((int)viewportHUD.x, (int)viewportHUD.y, game.getVirtualWidthHUD(), game.getVirtualHeightHUD());
        }
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        stage.clear();
        stage.dispose();
    }

    @Override
    public void show() {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}
}
