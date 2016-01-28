package edu.cs328;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.ResolutionFileResolver;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;

import java.util.ArrayList;

/**
 * Created by KnightPickles on 1/22/16.
 */
public class GameScreen implements Screen {
    protected Game game;
    protected SpriteBatch batch;
    protected OrthographicCamera gameCamera;
    protected OrthographicCamera HUDCamera;
    protected Stage stage;
    protected Skin skin;

    private Renderer renderer;

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

    public GameScreen(Game game, boolean fixed, boolean interpolated) {
        this.game = game;
        batch = game.getSpritebatch();
        gameCamera = game.getGAMECamera();
        HUDCamera = game.getHUDCamera();

        stage = new Stage();
        stage.getViewport().setCamera(HUDCamera);
        Gdx.input.setInputProcessor(stage);
        renderer = new Renderer(game, this);

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
        //for (i = 0; i < BOXCOUNT; i++) boxes.get(i).update(delta);
        //box2dWorld.step(delta * 1f, 10, 8);
    }

    public void rendering(float delta) {
        renderer.renderHUD();
        game.updateHUDCam();
        stage.act();
        stage.draw();
    }

    /*public void setupButtons() {
        // Table -> stage.addActor(), TextButton -> .addListener(...), tableName.add(textButtonName);

        Table menuTable = new Table();
        stage.addActor(menuTable);
        menuTable.setPosition(400, 460);
        menuTable.debug().defaults().space(6);
        skin = Assets.getSkin();
        TextButton btnPlayMenu = new TextButton(showVariableTimeStep, skin);
        btnPlayMenu.addListener(new ActorGestureListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);

                game.setScreen(new GameScreen(game, false, false));
            }});
        menuTable.add(btnPlayMenu).width(BUTTONWIDTH).height(BUTTONHEIGHT);

    }*/

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
